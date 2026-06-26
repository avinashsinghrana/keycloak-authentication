package com.asr.auth.mapper;

import com.asr.auth.domain.entity.RegisteredDevice;
import com.asr.auth.domain.enums.DeviceStatus;
import com.asr.auth.dto.request.RegisterRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-06-27T02:37:08+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 21.0.7 (Eclipse Adoptium)"
)
@Component
public class DeviceMapperImpl implements DeviceMapper {

    @Override
    public RegisteredDevice toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        RegisteredDevice.RegisteredDeviceBuilder registeredDevice = RegisteredDevice.builder();

        registeredDevice.deviceId( request.getDeviceId() );
        registeredDevice.deviceName( request.getDeviceName() );

        registeredDevice.status( DeviceStatus.PENDING_VERIFICATION );
        registeredDevice.isTrusted( false );

        return registeredDevice.build();
    }
}
