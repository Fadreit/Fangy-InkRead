package com.fadreit.inkreadboot.Mapper;

import com.fadreit.inkreadboot.Entity.Book;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface BookMapper {
    /**
     * 添加图书
     * @param book
     * 开启自动回填id
     */
    void addBook(Book book);

    /**
     * 管理员分页查询图书
     * @param keyword
     * @param categoryId
     * @param status
     * @return
     */
    List<Book> PageListBooks(String keyword, Integer categoryId, Integer status);

    /**
     * 用户分页查询图书
     * @param keyword
     * @param categoryId
     * @param sort
     * @return
     */
    List<Book> PageShowBooks(String keyword, Integer categoryId, String sort);

    /**
     * 根据id查询图书
     * @param id
     * @return
     */
    Book getBookById(Integer id);

    /**
     * 修改图书
     *
     * @param book
     */
    void updateBook(Book book);


    /**
     * 软删除图书
     * @param id
     */
    void updateBookIsDeleted(Integer id);

    /**
     * 修改图书上下架
     * @param id
     * @param status
     */
    void updateBookShelf(Integer id, Integer status, LocalDateTime updatedAT);

    /**
     * 修改图书库存
     * @param nowStock
     * @param bookId
     */
    void updateBookStock(int nowStock, Integer bookId);
}
