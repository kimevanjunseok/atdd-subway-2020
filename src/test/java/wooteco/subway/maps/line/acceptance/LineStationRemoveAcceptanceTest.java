package wooteco.subway.maps.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.common.acceptance.AcceptanceTest;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.maps.line.acceptance.step.LineAcceptanceStep;
import wooteco.subway.maps.line.acceptance.step.LineStationAcceptanceStep;
import wooteco.subway.maps.station.acceptance.step.StationAcceptanceStep;

import java.util.Arrays;

import static wooteco.subway.maps.line.acceptance.step.LineAcceptanceStep.지하철_노선_조회_요청;

@DisplayName("지하철 노선에 역 제외 관련 기능")
public class LineStationRemoveAcceptanceTest extends AcceptanceTest {
    private Long lineId;
    private Long stationId1;
    private Long stationId2;
    private Long stationId3;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        ExtractableResponse<Response> createLineResponse = LineAcceptanceStep.지하철_노선_등록되어_있음("2호선", "GREEN", "0");
        ExtractableResponse<Response> createdStationResponse1 = StationAcceptanceStep.지하철역_등록되어_있음("강남역");
        ExtractableResponse<Response> createdStationResponse2 = StationAcceptanceStep.지하철역_등록되어_있음("역삼역");
        ExtractableResponse<Response> createdStationResponse3 = StationAcceptanceStep.지하철역_등록되어_있음("선릉역");

        lineId = createLineResponse.as(LineResponse.class).getId();
        stationId1 = createdStationResponse1.as(StationResponse.class).getId();
        stationId2 = createdStationResponse2.as(StationResponse.class).getId();
        stationId3 = createdStationResponse3.as(StationResponse.class).getId();

        LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음(lineId, null, stationId1);
        LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음(lineId, stationId1, stationId2);
        LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음(lineId, stationId2, stationId3);
    }

    @DisplayName("지하철 노선에 등록된 마지막 지하철역을 제외한다.")
    @Test
    void removeLineStation1() {
        // when
        ExtractableResponse<Response> deleteResponse = LineStationAcceptanceStep.지하철_노선에_지하철역_제외_요청(lineId, stationId3);

        // then
        LineStationAcceptanceStep.지하철_노선에_지하철역_제외됨(deleteResponse);

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_조회_요청(lineId);

        // then
        LineStationAcceptanceStep.지하철_노선에_지하철역_제외_확인됨(response, stationId3);
        LineStationAcceptanceStep.지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(stationId1, stationId2));
    }

    @DisplayName("지하철 노선에 등록된 중간 지하철역을 제외한다.")
    @Test
    void removeLineStation2() {
        // when
        ExtractableResponse<Response> deleteResponse = LineStationAcceptanceStep.지하철_노선에_지하철역_제외_요청(lineId, stationId2);

        // then
        LineStationAcceptanceStep.지하철_노선에_지하철역_제외됨(deleteResponse);

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_조회_요청(lineId);

        // then
        LineStationAcceptanceStep.지하철_노선에_지하철역_제외_확인됨(response, stationId2);
        LineStationAcceptanceStep.지하철_노선에_지하철역_순서_정렬됨(response, Arrays.asList(stationId1, stationId3));
    }

    @DisplayName("지하철 노선에서 등록되지 않는 역을 제외한다.")
    @Test
    void removeLineStation5() {
        // when
        ExtractableResponse<Response> response = LineStationAcceptanceStep.지하철_노선에_지하철역_제외_요청(lineId, 20L);

        // then
        LineStationAcceptanceStep.지하철_노선에_지하철역_제외_실패됨(response);
    }
}
