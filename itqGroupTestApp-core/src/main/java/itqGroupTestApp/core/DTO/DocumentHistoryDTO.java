package itqGroupTestApp.core.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "Модель документа с историей")
public class DocumentHistoryDTO implements Serializable{

    @Schema(description = "Документ")
    public DocumentDTO documentDTO;
    @Schema(description = "Список истории документа")
    public List<HistoryDTO> historyDTOList;

    public DocumentHistoryDTO(DocumentDTO documentDTO, List<HistoryDTO> historyDTOList) {
        this.documentDTO = documentDTO;
        this.historyDTOList = historyDTOList;
    }
}
