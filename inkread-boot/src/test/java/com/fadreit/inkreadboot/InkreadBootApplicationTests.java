package com.fadreit.inkreadboot;

import com.fadreit.inkreadboot.Common.Result;
import com.fadreit.inkreadboot.Entity.Book;
import com.fadreit.inkreadboot.Mapper.BookMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class InkreadBootApplicationTests {

//    @Autowired
//    private BookMapper bookMapper;
//
//    @Test
//    void TestAddBook(){
//        Book book1 = new Book();
//        book1.setTitle("Spring实战");
//        book1.setAuthor("Craig Walls");
//        book1.setIsbn("978-7-115-12345-6");
//        book1.setCategoryId(1);
//        book1.setPrice(new BigDecimal("79.00"));
//        book1.setStock(50);
//        book1.setPublisher("人民邮电出版社");
//        book1.setPublishDate(LocalDate.of(2019, 3, 15));
//        book1.setDescription("Spring框架权威指南");
//        book1.setCoverUrl("https://example.com/spring.jpg");
//        book1.setCreatedAt(LocalDateTime.now());
//        book1.setUpdatedAt(LocalDateTime.now());
//        book1.setStatus(1);
//        book1.setIsDeleted(0);
//        bookMapper.addBook(book1);
//        System.out.println("图书1 ID: " + book1.getId());
//    }

}
