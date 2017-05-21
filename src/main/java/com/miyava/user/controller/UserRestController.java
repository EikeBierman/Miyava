package com.miyava.user.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.miyava.user.model.User;
import com.miyava.user.service.UserDao;

@RestController
@RequestMapping( UserRestController.BASE_URL )
public class UserRestController {

    protected final static String BASE_URL = UserController.BASE_URL + "/data";

    @Autowired
    private UserDao userDao;

    @JsonView( DataTablesOutput.View.class )
    @RequestMapping( value = "/users", method = RequestMethod.GET )
    public DataTablesOutput<User> getUsers( @Valid DataTablesInput input ) {
        return userDao.findAll( input );
    }

}
