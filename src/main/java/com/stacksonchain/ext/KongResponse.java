package com.stacksonchain.ext;

import java.util.List;
import lombok.Data;

@Data
public class KongResponse<T> {

  List<T> data;
}
