package com.miyava.user.dto;

import com.miyava.common.EntityToDTOMapper;
import com.miyava.user.model.User;

public class UserDTOMapper
    implements EntityToDTOMapper<User, UserDTO> {

    @Override
    public UserDTO apply( User input ) {
        UserDTO dto = new UserDTO();
        dto.setId( input.getId() );
        dto.setUserName( input.getUsername() );

        return dto;
    }

}
