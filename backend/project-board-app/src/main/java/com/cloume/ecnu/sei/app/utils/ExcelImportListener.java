package com.cloume.ecnu.sei.app.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cloume.ecnu.sei.app.service.IBaseService;
import lombok.Getter;
import lombok.val;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 读取excel并批量导入到db
 */
public class ExcelImportListener<D, T> extends AnalysisEventListener<D> {
    /**
     * 批量插入的单次上限
     */
    private static final int BATCH_COUNT = 1000;
    private List<T> cache = new ArrayList<>();
    private final IBaseService<T> service;
    private final Logger logger;
    private final Function<D, T> mapper;

    @Getter
    private long successCount = 0;

    public ExcelImportListener(Logger logger, IBaseService<T> service, Function<D, T> mapper) {
        this.service = service;
        this.mapper = mapper;
        this.logger = logger;
    }

    @Override
    public void invoke(D d, AnalysisContext analysisContext) {
        T dbModel = mapper.apply(d);
        cache.add(dbModel);
        if (cache.size() >= BATCH_COUNT) {
            saveData();
            cache = new ArrayList<>();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
    }

    public void saveData() {
        try {
            val result = (List<T>) service.save(cache);
            successCount += result.size();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
