//package com.cloume.ecnu.llb.app.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;
//import springfox.documentation.swagger2.annotations.EnableSwagger2;
//
//@Configuration
//@EnableSwagger2
//public class SwaggerConfig {
//    public static final String SWAGGER_SCAN_BASE_PACKAGE = "com.cloume.ecnu.llb.app.controller";
//
//    public static final String VERSION = "1.0.0";
//
//    @Bean
//    public Docket createRestApi() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
//                .paths(PathSelectors.any()) // 可以根据url路径设置哪些请求加入文档，忽略哪些请求
//                .build();
//    }
//
//    /**
//     * 接口文档详细信息
//     *
//     * @return
//     */
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("第二课堂Api") //设置文档的标题
//                .description("第二课堂 API 接口文档") // 设置文档的描述
//                .version(VERSION) // 设置文档的版本信息-> 1.0.0 Version information
//                .termsOfServiceUrl("192.168.1.191:8080/sca") // 设置文档的License信息->1.3 License information
//                .build();
//    }
//
//}
