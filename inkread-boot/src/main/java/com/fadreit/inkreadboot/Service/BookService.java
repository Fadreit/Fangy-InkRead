package com.fadreit.inkreadboot.Service;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Book_PageListBook;
import com.fadreit.inkreadboot.Dto.Book_PageShowBooks;
import com.fadreit.inkreadboot.Entity.Book;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {

    /**
     * 上传图书封面
     * @param file 文件
     * @return 响应结果
     */
    Result uploadBookCover(MultipartFile file);

    /**
     * 添加图书
     * @param book 图书
     * @return 响应结果
     */
    Result addBook(Book book);

    /**
     * 管理员查询图书列表
     * @param bp 图书参数
     * @return 响应结果
     */
    Result PageListBooks(Book_PageListBook bp);

    /**
     * 用户查询图书列表
     * @return 响应结果
     */
    Result PageShowBooks(Book_PageShowBooks bp);

    /**
     * 根据id查询图书
     * @param id 图书id
     * @return 响应结果
     */
    Result getBookById(Integer id);

    /**
     * 修改图书
     * @param book 图书
     * @param id 图书id
     * @return 响应结果
     */
    Result updateBook(Book book, Integer id);

    /**
     * 删除图书
     * @param id 图书id
     * @return 响应结果
     */
    Result deleteBook(Integer id);

    /**
     * 修改图书上下架
     * @param id 图书id
     * @param status 图书架子状态
     * @return 响应结果
     */
    Result updateBookShelf(Integer id, Integer status);
}
