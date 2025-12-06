package com.nexus.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页响应结果封装类
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> {

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Long current;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 私有构造方法
     */
    private PageResult() {
    }

    /**
     * 私有构造方法
     *
     * @param records 数据列表
     * @param total   总记录数
     * @param current 当前页码
     * @param size    每页大小
     * @param pages   总页数
     */
    private PageResult(List<T> records, Long total, Long current, Long size, Long pages) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = pages;
    }

    /**
     * 从 MyBatis-Plus 的 IPage 对象创建 PageResult
     *
     * @param page MyBatis-Plus 的 IPage 对象
     * @param <T>  数据类型
     * @return PageResult<T>
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(
                page.getRecords(),
                page.getTotal(),
                page.getCurrent(),
                page.getSize(),
                page.getPages()
        );
    }

    /* * @param <T> 数据类型
     * @param records 当前页的数据列表
     * @param total 总记录数
     * @param current 当前页码（从1开始）
     * @param size 每页大小
     * @return 包含分页信息和数据的 PageResult<T> 实例
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        return new PageResult<>(
                records,
                total,
                current,
                size,
                (total + size - 1) / size // 自动计算总页数
        );
    }
}