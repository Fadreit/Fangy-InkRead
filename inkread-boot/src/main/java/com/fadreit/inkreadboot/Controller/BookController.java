package com.fadreit.inkreadboot.Controller;

import com.aliyun.oss.model.MultipartUpload;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Book_PageListBook;
import com.fadreit.inkreadboot.Dto.Book_PageShowBooks;
import com.fadreit.inkreadboot.Entity.Book;
import com.fadreit.inkreadboot.Service.BookService;
import com.fadreit.inkreadboot.Util.AliyunOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
public class BookController {

    //注入Service
    @Autowired
    private BookService bookService;

    /**
     * 添加图书
     * @param book
     * @return
     * 需要admin权限
     */
    @PostMapping("/api/admin/books")
    public Result addBook(@RequestBody Book book) {
        //日志记录
        log.info("添加图书：{}", book);
        //调用Service
        Result result = bookService.addBook(book);
        return result;
    }

    /**
     * 上传图书封面
     * @param file
     * @return
     * 需要admin权限
     */
    @PostMapping("/api/admin/books/upload")
    public Result uploadBookCover(MultipartFile file){
        //日志记录
        log.info("上传图书封面：{}", file);
        //调用Service
        Result result = bookService.uploadBookCover(file);
        //返回结果
        return result;
    }

    /**
     * 分页查询图书
     * @param bp
     * @return
     * 需要admin权限
     */
    @GetMapping("/api/admin/books")
    public Result PageListBooks(Book_PageListBook bp){
        //日志记录
        log.info("管理员分页查询图书：{}", bp);
        Result result = bookService.PageListBooks(bp);
        return result;
    }

    /**
     * 用户分页查询图书
     * @param bp
     * @return
     */
    @GetMapping("/api/books")
    public Result PageShowBooks(Book_PageShowBooks bp){
        //日志记录
        log.info("用户分页查询图书");
        //调用Service
        Result result = bookService.PageShowBooks(bp);
        return result;
    }

    /**
     * 根据id查询图书
     * @param id
     * @return
     */
    @GetMapping("/api/books/{id}")
    public Result getBookById(@PathVariable Integer id){
        //日志记录
        log.info("查询图书：{}", id);
        return bookService.getBookById(id);
    }

    /**
     * 修改图书
     * @param book
     * @param id
     * @return
     * 需要admin权限
     */
    @PutMapping("/api/admin/books/{id}")
    public Result updateBook(@RequestBody Book book, @PathVariable Integer id){
        //日志记录
        log.info("修改图书：{}", book);
        return bookService.updateBook(book, id);
    }

    @DeleteMapping("/api/admin/books/{id}")
    public Result deleteBook(@PathVariable Integer id){
        //日志记录
        log.info("软删除图书：{}", id);
        return bookService.deleteBook(id);
    }

    @PutMapping("/api/admin/books/{id}/shelf")
    public Result updateBookShelf(@PathVariable Integer id, @RequestParam Integer status){
        //日志记录
        log.info("修改图书上下架：{}", id);
        return bookService.updateBookShelf(id, status);
    }
}
