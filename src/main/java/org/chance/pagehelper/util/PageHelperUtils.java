package org.chance.pagehelper.util;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页 工具类( 使用PageHelper)
 *
 * @author GengChao &lt; catchance@163.com &gt;
 * @date 2019/8/1
 */
@Slf4j
public class PageHelperUtils {

    /**
     * PageInfo对象转换工具类
     *
     * @param pageInfo 原来的PageInfo分页数据
     * @return
     */
    public static <T, R> PageInfo<R> transform(PageInfo<T> pageInfo, Class<R> clazz) {

        if (pageInfo == null || CollectionUtils.isEmpty(pageInfo.getList())) {
            return emptyPageInfo();
        }

        PageInfo<R> result = new PageInfo<>();

        BeanUtils.copyProperties(pageInfo, result);
        List<R> resultList = pageInfo.getList().stream().map(t -> {
            R r = null;
            try {
                r = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            BeanUtils.copyProperties(t, r);
            return r;
        }).collect(Collectors.toList());
        result.setList(resultList);

        return result;
    }

    /**
     * PageInfo对象转换工具类
     *
     * @param pageInfo 原来的PageInfo分页数据
     * @return
     */
    public static <T, R> PageInfo<R> transform(PageInfo<T> pageInfo, Function<? super T, ? extends R> mapper) {

        if (pageInfo == null || CollectionUtils.isEmpty(pageInfo.getList())) {
            return emptyPageInfo();
        }

        if (mapper == null) {
            throw new IllegalArgumentException("PageInfo对象转换工具类 mapper 不能为null");
        }

        PageInfo<R> result = new PageInfo<>();

        BeanUtils.copyProperties(pageInfo, result);
        List<R> resultList = pageInfo.getList().stream().map(mapper).collect(Collectors.toList());
        result.setList(resultList);

        return result;
    }

    /**
     * 排序功能
     *
     * @param orderBy 排序
     */
    public static void orderBy(String orderBy) {
        PageHelper.orderBy(orderBy);
    }

    /**
     * 开启分页 开启分页后的第一个dao的调用会开启分页功能
     *
     * @param pageable 分页参数
     */
    public static void startPage(Pageable pageable) {
        startPage(pageable, true);
    }

    /**
     * 开启分页 开启分页后的第一个dao的调用会开启分页功能
     *
     * @param pageable 分页参数
     */
    public static void startPage(Pageable pageable, String orderBy) {
        startPage(pageable, true);
        orderBy(orderBy);
    }

    /**
     * 开启分页 开启分页后的第一个dao的调用会开启分页功能
     *
     * @param pageable 分页参数
     * @param count    是否进行count查询
     */
    public static void startPage(Pageable pageable, boolean count) {
        if (pageable != null && pageable.isPaged()) {
            PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize(), count, false, null);
        }
    }

    /**
     * 构建一个空的PageInfo
     **/
    public static PageInfo emptyPageInfo() {
        return PageInfo.of(Collections.emptyList());
    }

}
