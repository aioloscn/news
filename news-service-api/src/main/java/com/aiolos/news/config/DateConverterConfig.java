package com.aiolos.news.config;

import com.aiolos.news.common.enums.ErrorEnum;
import com.aiolos.news.common.exception.CustomizedException;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 自动将请求路径url中的字符串参数进行时间日期类型的转换
 * @author Aiolos
 * @date 2021/5/8 7:53 下午
 */
@Configuration
public class DateConverterConfig implements Converter<String, Date> {

    private static final List<String> formatterList = new ArrayList<>(4);
    static{
        formatterList.add("yyyy-MM");
        formatterList.add("yyyy-MM-dd");
        formatterList.add("yyyy-MM-dd hh:mm");
        formatterList.add("yyyy-MM-dd hh:mm:ss");
    }

    @SneakyThrows
    @Override
    public Date convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if(source.matches("^\\d{4}-\\d{1,2}$")){
            return parseDate(source, formatterList.get(0));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")){
            return parseDate(source, formatterList.get(1));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formatterList.get(2));
        }else if(source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")){
            return parseDate(source, formatterList.get(3));
        }else {
            throw new CustomizedException(ErrorEnum.SYSTEM_DATE_PARSER_ERROR);
        }
    }

    /**
     * 日期转换方法
     * @param dateStr
     * @param formatter
     * @return
     */
    public Date parseDate(String dateStr, String formatter) {
        Date date=null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(formatter);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
