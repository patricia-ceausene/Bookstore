package com.db.bookstore.controller;

import com.db.bookstore.model.Author;
import com.db.bookstore.model.Book;
import com.db.bookstore.model.User;

import com.db.bookstore.service.AuthorService;
import com.db.bookstore.service.BookService;
import com.db.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    BookService bookService;

    @Autowired
    AuthorService authorService;

    @GetMapping("/register")
    public ModelAndView getRegisterForm(){
        ModelAndView modelAndView = new ModelAndView("register-form");
        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView addUser(User user){
        user.setRole("client");
        userService.insertUser(user);
        ModelAndView modelAndView = new ModelAndView("redirect:/login");
        return modelAndView;
    }

    @GetMapping("/login")
    public ModelAndView getLoginForm(){
        ModelAndView modelAndView = new ModelAndView("login-form");
        return modelAndView;
    }

    @PostMapping("/login")
    public ModelAndView verifyUser(User user, HttpServletResponse response){
        try {
            User user1 = userService.findByUsernameOrEmailAndPassword(user);
            response.addCookie(new Cookie("id", "" + user1.getId()));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        return modelAndView;
    }

    @GetMapping("/dashboard")
    public ModelAndView getDashBoard(@CookieValue(name="id",defaultValue = "default") int userId){
        ModelAndView modelAndView=new ModelAndView("dashboard");
        List<Book> bookList = bookService.getAll();
        modelAndView.addObject("bookList",bookList);
        User user = userService.findById(userId);
        modelAndView.addObject("username",user.getUsername());
        return modelAndView;
    }

    @GetMapping("/add-book")
    public ModelAndView getAddBookForm(@CookieValue(name="id",defaultValue = "default") int userId){
        User user = userService.findById(userId);
        if(user.getRole().equals("client")) {
            ModelAndView modelAndView = new ModelAndView("error-message");
            modelAndView.addObject("errorMessage","You don`t have the authorization to add books.");
            return modelAndView;
        } else {
            ModelAndView modelAndView = new ModelAndView("add-book");
            List<Author> authorList = authorService.getAll();
            modelAndView.addObject("authorsList", authorList);
            return modelAndView;
        }
    }

    @PostMapping("/add-book")
    public ModelAndView addBook(Book book){
        bookService.insertBook(book);
        ModelAndView modelAndView = new ModelAndView("redirect:/dashboard");
        modelAndView.addObject(book);
        return modelAndView;
    }

}
