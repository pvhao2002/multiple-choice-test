package com.app.quizzservice.request.response;

import java.util.List;

public record ChartData(
        List<String> labels,
        List<Datasets> datasets
) {

}
