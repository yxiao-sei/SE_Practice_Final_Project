package com.cloume.ecnu.sei.app.controller;

import com.cloume.commons.rest.response.RestResponse;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.font.FontProvider;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static java.lang.System.out;

/**
 * @author yxiao
 * @date
 * @description 文件存取接口
 */
@Log4j2
@RestController
@Component
@RequestMapping("/app/file")
//@Api(tags = "文件存取接口")
public class FileController {

    @Value("${minio.endpoint:http://127.0.0.1:9004}")
    private String endpoint;

    @Value("${minio.accessKey:xgb_sca}")
    private String accessKey;

    @Value("${minio.accessValue:pl,okm123}")
    private String accessValue;

    @Value("${minio.bucket:sca}")
    private String bucket;

    @Value(("#{'${minio.fileType}'.split(',')}"))
    private List<String> whiteFileTypeList;

    private MinioClient minioClient;

    @Value("${templateUrl.url}")
    private String templateUrl;

    @Value(("#{'${minio.imgType}'.split(',')}"))
    private List<String> imgTypeList;

    @Value("${templateUrl.fileUrl}")
    private String templateFileUrl;

    private IConverter pdfConverter;

    private boolean msOfficeNotSupport;

    /**
     * 文件上传接口
     *
     * @param principal
     * @param file
     * @return
     */
    @PutMapping
    public RestResponse<?> upload(Principal principal, @RequestParam MultipartFile file) {
        if (file == null) {
            return RestResponse.bad(-1, "文件为空，请重新上传");
        }
        try {
            String suffix = ""; //文件格式类型
            if (file.getOriginalFilename().contains(".")) {
                String[] split = file.getOriginalFilename().split("\\.");
                suffix = split[split.length - 1];
            }
            if (!whiteFileTypeList.contains(suffix)) {
                return RestResponse.bad(-3, "不支持的文件类型，请勿上传！");
            }
            String fileName = String.format("%s-%s", principal.getName(), file.getOriginalFilename());

            ObjectWriteResponse objectWriteResponse = minioUpload(file.getInputStream(), fileName);
            if (objectWriteResponse == null) {
                return RestResponse.bad(-2, "文件上传异常，请联系管理员处理");
            }
            return RestResponse.good(objectWriteResponse.object());
        } catch (Exception e) {
            log.error("文件上传失败：{}", e);
            return RestResponse.bad(-2, "文件上传异常，请联系管理员处理");
        }
    }

    /**
     * 将文件上传至minio
     *
     * @param fileInputStream
     * @param fileName        上传文件名
     * @return
     */
    private ObjectWriteResponse minioUpload(InputStream fileInputStream, String fileName) {
        try {
            if (minioClient == null) {
                minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, accessValue).build();
                boolean bucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!bucketExist) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("minio init success, bucket: {}", bucket);
                }
            }
            ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(fileName).stream(fileInputStream, -1, 10485760).build());
            return objectWriteResponse;
        } catch (Exception e) {
            log.error("文件上传失败：{}", e);
            return null;
        }
    }

    /**
     * 文件下载
     *
     * @param fileName
     * @param response
     */
    @GetMapping
    public void download(@RequestParam(defaultValue = "fileName") String fileName, HttpServletResponse response) {
        try {
            List<String> files = Arrays.asList(fileName.split(","));
            for (String file : files) {
                InputStream inputStream = getFileInputStream(file);
                byte buf[] = new byte[1024];
                int length = 0;
                response.reset();
                response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file, "UTF-8"));
                response.setContentType("application/octet-stream");
                response.setCharacterEncoding("utf-8");
                OutputStream outputStream = response.getOutputStream();
                while ((length = inputStream.read(buf)) > 0) {
                    outputStream.write(buf, 0, length);
                }
                outputStream.close();
            }
        } catch (Exception e) {
            log.error("文件下载异常：{}, {}", fileName, e);
        }
    }

    /**
     * 文件下载（压缩包）
     *
     * @param fileNames
     * @param response
     */
    @GetMapping("/download/zip")
    public RestResponse<?> downloadZIPCommon(@RequestParam(defaultValue = "", required = true) String fileNames, HttpServletResponse response) throws IOException {
        List<String> fileIds = Arrays.asList(fileNames.split(","));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String path = simpleDateFormat.format(new Date()) + ".zip";
        response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(path, "UTF-8") + "\"");
        ZipOutputStream zipOutput = new ZipOutputStream(response.getOutputStream());
        for (String fileName : fileIds) {
            try {
                InputStream inputStream = getFileInputStream(fileName);
                zipOutput.putNextEntry(new ZipEntry(fileName));
                zipOutput.write(toByteArray(inputStream));
            } catch (Exception e) {
                log.error("文件下载异常：{}", e);
                return RestResponse.bad(-3, "文件下载异常");
            }
        }
        zipOutput.close();
        return RestResponse.bad(-3, "文件下载异常");
    }


    @GetMapping("/url")
    public RestResponse<?> downloadUrl(@RequestParam(defaultValue = "fileName") String fileName, HttpServletResponse response) {
        String str = "";
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            InputStream inputStream = getFileInputStream(fileName);
            str = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucket).object(fileName).expiry(60 * 60 * 24).build());
            byte[] bytes = new byte[1024 * 1024];
            int size = 0;
            int len = 0;
            while ((len = inputStream.read(bytes)) != -1) {
                size += len;
            }
            result.put("url", str);
            result.put("size", size);
        } catch (Exception e) {
            log.error("文件下载异常：{}", e);
        }
        return RestResponse.good(result);
    }

    /**
     * 根据文件名从minio获取InputStream
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    private InputStream getFileInputStream(String fileName) throws Exception {
        if (minioClient == null) {
            minioClient = MinioClient.builder().endpoint(endpoint).credentials(accessKey, accessValue).build();
            boolean bucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!bucketExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("minio init success, bucket: {}", bucket);
            }
        }
        log.info("get file: {}", fileName);
        InputStream inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(fileName).build());
        return inputStream;
    }

    /**
     * InputStream 转换成byte[]
     *
     * @param input
     * @return
     * @throws IOException
     */
    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    //    @ApiOperation("根据文件名下载 导入模板")
    @GetMapping("/downloadTemplate")
    public RestResponse<?> downloadTemplate(HttpServletResponse response, @RequestParam(defaultValue = "") String fileName) throws IOException {
        //调价明细导入模板.xlsx
        String fileUrl = templateUrl + fileName;
        //将文件转为ByteArrayOutputStream返回
        ByteArrayOutputStream byteArrayOutputStream = this.getBytesArray(fileUrl);
        response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
        response.setContentLength(byteArrayOutputStream.size());
        OutputStream out = response.getOutputStream();
        byteArrayOutputStream.writeTo(out);
        out.flush();
        out.close();
        return RestResponse.good("ok");
    }


    /**
     * 将文件转为ByteArrayOutputStream
     *
     * @param filePath 文件路径
     * @return
     */
    public static ByteArrayOutputStream getBytesArray(String filePath) {
        File file = new File(filePath);
        ByteArrayOutputStream out = null;
        try {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            out = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int i = 0;
            while ((i = in.read(b)) != -1) {
                out.write(b, 0, b.length);
            }
            out.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * 导出文件，并把word转成pdf，再给pdf加水印
     *
     * @param fileName 文件名
     * @param response 响应体
     */
    @GetMapping("/mark")
    public void downloadWithMark(@RequestParam(defaultValue = "fileName") String fileName, HttpServletResponse response) throws Exception {
        try (val out = response.getOutputStream(); val file = getFileInputStream(fileName)) {
            response.setCharacterEncoding("utf-8");
            val isPdf = fileName.endsWith(".pdf");
            val isDoc = fileName.endsWith(".doc") || fileName.endsWith(".docx");
            // 如果是word文档则转为pdf
            fileName = URLEncoder.encode(fileName.replaceAll("\\.doc(x)?$", ".pdf"), "UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            // 不符合doc或pdf格式，直接返回文件
            if (!isPdf && !isDoc) {
                response.setContentType("application/octet-stream");
                IOUtil.copyCompletely(file, out);
                return;
            }
            // 可以加水印，则最终返回pdf
            response.setContentType("application/pdf");
            val pdf = isPdf ? file : convertDocToPdf(file);
            if (pdf == null) {
                throw new Exception("pdf转换失败");
            }
            // 添加水印
            try (val reader = new PdfReader(pdf); val writer = new PdfWriter(out); val pdfDocument = new PdfDocument(reader, writer)) {
                addImageWatermark(pdfDocument);
            }
        } catch (Exception e) {
            log.error(e);
            val out = response.getOutputStream();
            val file = getFileInputStream(fileName);
            response.setContentType("application/octet-stream");
            IOUtil.copyCompletely(file, out);
            return;
        }
    }

    /**
     * 获取水印图片
     *
     * @return 字节数组
     */
    private byte[] getWaterMark() {
        try (val file = getClass().getClassLoader().getResourceAsStream("file/校标.png")) {
            val outputStream = new ByteArrayOutputStream();
            if (file != null) {
                IOUtil.copyCompletely(file, outputStream);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error(e);
        }
        return new byte[0];
    }

    /**
     * 添加图片水印
     *
     * @param pdfDocument 需操作的pdf文档
     */
    private void addImageWatermark(PdfDocument pdfDocument) {
        val watermarkBytes = getWaterMark();
        // 获取PDF页面数
        int numberOfPages = pdfDocument.getNumberOfPages();
        // 遍历每一页并添加水印
        for (int i = 1; i <= numberOfPages; i++) {
            PdfPage page = pdfDocument.getPage(i);

            // 创建水印图片对象
            val watermarkImage = new Image(ImageDataFactory.create(watermarkBytes));

            // 设置水印图片的透明度
            watermarkImage.setOpacity(0.2f);

            // 设置水印图片的倾斜角度（30°）
            watermarkImage.setRotationAngle(Math.toRadians(30));

            // 获取页面大小
            float pageWidth = page.getPageSize().getWidth();
            float pageHeight = page.getPageSize().getHeight();

            // 获取水印图片大小
            float imageWidth = watermarkImage.getImageWidth();
            float imageHeight = watermarkImage.getImageHeight();

            // 计算缩放比例，确保水印图片适合页面大小
            float scale = 0.7f * Math.min(pageWidth / imageWidth, pageHeight / imageHeight);

            // 缩放水印图片
            watermarkImage.scale(scale, scale);

            // 计算水印图片在页面上的起始坐标
            float x = (pageWidth - watermarkImage.getImageScaledWidth()) / 2;
            float y = (pageHeight - watermarkImage.getImageScaledHeight()) / 2;
            watermarkImage.setFixedPosition(x, y);

            val pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDocument);
            try (val canvas = new Canvas(pdfCanvas, page.getPageSize())) {
                // 添加水印
                canvas.add(watermarkImage);
            }
        }
    }

    /**
     * 使用本地ms office 或 poi来将word转换为pdf
     *
     * @param doc word文档
     * @return pdf文档
     */
    private InputStream convertDocToPdf(InputStream doc) {
        try (val out = new ByteArrayOutputStream()) {
            startMSOfficeConverter();
            // 可以使用ms office
            if (!msOfficeNotSupport) {
                val success = pdfConverter.convert(doc).as(DocumentType.MS_WORD).to(out).as(DocumentType.PDF).execute();
                if (success) {
                    return new ByteArrayInputStream(out.toByteArray());
                }
            }
        } catch (Exception e) {
            log.error("ms office转换失败：{}", e);
        }
        // ms office 失败，改用poi进行转换
        try (val poiOut = new ByteArrayOutputStream()) {
            val xwpfDocument = new XWPFDocument(doc);
            val pdfOptions = PdfOptions.create();
            val converter = PdfConverter.getInstance();
            converter.convert(xwpfDocument, poiOut, pdfOptions);
            return new ByteArrayInputStream(poiOut.toByteArray());
        } catch (Exception e) {
            log.error("poi 转换也失败：{}", e);
        }
//
//        try {
//            val poiOut = new ByteArrayOutputStream()
//            val hwpfDocument = new HWPFDocument(doc);
//            val pdfOptions = PdfOptions.create();
//            val converter = new PdfConverter();
//            converter(hwpfDocument, poiOut, pdfOptions);
//            return new ByteArrayInputStream(poiOut.toByteArray());
//        } catch (Exception e) {
//            log.error(e);
//        })

        return doc;
    }

    @PreDestroy
    private void shutdownConverter() {
        if (pdfConverter != null && pdfConverter.isOperational()) {
            pdfConverter.shutDown();
        }
    }

    private void startMSOfficeConverter() {
        if (msOfficeNotSupport) {
            return;
        }
        try {
            if (pdfConverter == null || !pdfConverter.isOperational()) {
                pdfConverter = LocalConverter.builder().baseFolder(null).workerPool(20, 25, 2, TimeUnit.SECONDS).processTimeout(10, TimeUnit.SECONDS).build();
            }
        } catch (Exception e) {
            log.error(e);
            msOfficeNotSupport = true;
        }

    }
}
