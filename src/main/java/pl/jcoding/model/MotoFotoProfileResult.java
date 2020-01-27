package pl.jcoding.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MotoFotoProfileResult {
    private Long id;
    private String name;
    private String nickname;
}
