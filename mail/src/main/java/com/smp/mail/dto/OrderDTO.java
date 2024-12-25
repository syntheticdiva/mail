package com.smp.mail.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
public class OrderDTO {
    private String userEmail;
    private List<ItemDTO> items = new ArrayList<>();

}