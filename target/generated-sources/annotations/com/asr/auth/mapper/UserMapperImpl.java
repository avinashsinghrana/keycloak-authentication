package com.asr.auth.mapper;

import com.asr.auth.domain.entity.AppUser;
import com.asr.auth.domain.enums.UserStatus;
import com.asr.auth.dto.request.RegisterRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T01:18:53+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public AppUser toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        AppUser.AppUserBuilder appUser = AppUser.builder();

        appUser.email( request.getEmail() );
        appUser.mobile( request.getMobile() );

        appUser.status( UserStatus.PENDING_VERIFICATION );

        return appUser.build();
    }
}
