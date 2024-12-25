package com.smp.mail.dto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class OrderDTO {
    private String userEmail;
    private List<ItemDTO> items = new ArrayList<>();

}