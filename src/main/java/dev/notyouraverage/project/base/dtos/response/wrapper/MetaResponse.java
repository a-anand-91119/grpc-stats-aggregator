package dev.notyouraverage.project.base.dtos.response.wrapper;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.notyouraverage.project.base.constants.Constants;
import dev.notyouraverage.project.base.enums.ResponseStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MetaResponse(
        ResponseStatus status,

        @JsonFormat(pattern = Constants.RESPONSE_TIMESTAMP_FORMAT, shape = JsonFormat.Shape.STRING) LocalDateTime timestamp
) {

}
