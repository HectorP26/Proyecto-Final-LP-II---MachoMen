package com.machomen.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResultadoResponse {
    private boolean success;
    private String mensaje;

    public boolean isSuccess() {
        return success;
    }

    public String getMensaje() {
        return mensaje;
    }
}
