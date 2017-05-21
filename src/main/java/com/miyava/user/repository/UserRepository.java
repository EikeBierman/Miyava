package com.miyava.user.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import com.miyava.user.model.User;

public interface UserRepository
    extends DataTablesRepository<User, String> {

    User findOneByUsername( String username );
}
