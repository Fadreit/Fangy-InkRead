package com.fadreit.inkreadboot.Service.Impl;

import com.fadreit.inkreadboot.Common.PageResult;
import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Dto.Book_PageListBook;
import com.fadreit.inkreadboot.Dto.Book_PageShowBooks;
import com.fadreit.inkreadboot.Dto.Book_UpdateStatus;
import com.fadreit.inkreadboot.Entity.Book;
import com.fadreit.inkreadboot.Mapper.BookMapper;
import com.fadreit.inkreadboot.Service.BookService;
import com.fadreit.inkreadboot.Util.AliyunOSSUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.StreamHandler;
import java.util.stream.Stream;

@Component
public class BookServiceImpl implements BookService {
    //注入Mapper
    @Autowired
    private BookMapper bookMapper;

    @Override
    public Result uploadBookCover(MultipartFile file) {
        try {
            String fileUrl = AliyunOSSUtil.uploadFile(file);

            Map<String, String> data = new HashMap<>();
            data.put("url", fileUrl);

            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "文件上传失败");
        }
    }

    @Override
    public Result addBook(Book book) {
        //调用Mapper层
        //设置参数
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());
        book.setStatus(1);
        book.setIsDeleted(0);
        //增加图书
        bookMapper.addBook(book);
        //返回结果
        return Result.success(book);
    }

    @Override
    public Result PageListBooks(Book_PageListBook bp) {
        //开始分页
        PageHelper.startPage(bp.getPage(), bp.getSize());
        //查询
        List<Book> books = bookMapper.PageListBooks(bp.getKeyword(), bp.getCategoryId(), bp.getStatus());
        //返回结果
        Page<Book> pages = (Page<Book>)books;
        PageResult<Book> pageResult = new PageResult<>(pages.getTotal(),
                pages.getPages(), pages.getPageNum(), pages.getPageSize(), pages.getResult());
        return Result.success(pageResult);
    }

    @Override
    public Result PageShowBooks(Book_PageShowBooks bp) {
        //开始分页
        PageHelper.startPage(bp.getPage(), bp.getSize());
        //查询
        List<Book> books = bookMapper.PageShowBooks(bp.getKeyword(), bp.getCategoryId(), bp.getSort());
        //返回结果
        Page<Book> pages = (Page<Book>)books;
        PageResult<Book> pageResult = new PageResult<>(pages.getTotal(),
                pages.getPages(), pages.getPageNum(), pages.getPageSize(), pages.getResult());
        return Result.success(pageResult);
    }

    @Override
    public Result getBookById(Integer id) {
        //查询
        Book book = bookMapper.getBookById(id);
        if (book.getStatus() == 0 || book.getIsDeleted() == 1 || book.getTitle() == null){
            return Result.error(404, "图书不存在");
        }
        return Result.success(book);
    }

    @Override
    @Transactional
    public Result updateBook(Book book, Integer id) {
        book.setUpdatedAt(LocalDateTime.now());
        book.setId(id);
        //调用
        bookMapper.updateBook(book);
        //返回结果
        Book book1 = bookMapper.getBookById(id);
        return Result.success(book1);
    }

    @Override
    @Transactional
    public Result deleteBook(Integer id) {
        //查询
        Book book = bookMapper.getBookById(id);
        if (book.getIsDeleted() == 1){
            return Result.error(404, "图书不存在或已被删除");
        }
        bookMapper.updateBookIsDeleted(id);
        return Result.success();
    }

    @Override
    @Transactional
    public Result updateBookShelf(Integer id, Integer status) {
        //调用Mapper
        LocalDateTime updatedAT = LocalDateTime.now();
        bookMapper.updateBookShelf(id, status, updatedAT);
        //查询
        Book book = bookMapper.getBookById(id);
        Book_UpdateStatus bu = new Book_UpdateStatus();
        bu.setStatus(book.getStatus());
        bu.setId(book.getId());
        bu.setTitle(book.getTitle());
        //返回结果
        return Result.success(bu);
    }
}
