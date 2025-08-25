package com.mechtech.automach.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UserLoginDetailsDTO {
    private long appCode;
    private String userId;
    private long subscriberCode;
    private String mobileNo;
    private LocalDate subscriberExpiryDate;
    private long roleCode;
    private String roleName;
    private long locationCode;
    private String locationName;
    private String firmName;

    // Constructor for potential JPQL query usage
    public UserLoginDetailsDTO(long appCode, String userId, long subscriberCode, 
                            String mobileNo, 
                            LocalDate subscriberExpiryDate, long roleCode, 
                            String roleName, long locationCode, String locationName, 
                             String firmName) {
        this.appCode = appCode;
        this.userId = userId;
        this.subscriberCode = subscriberCode;
        this.mobileNo = mobileNo;
        this.subscriberExpiryDate = subscriberExpiryDate;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.firmName = firmName;
    }
}
