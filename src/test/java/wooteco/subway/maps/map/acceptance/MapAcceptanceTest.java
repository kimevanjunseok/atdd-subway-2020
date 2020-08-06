package wooteco.subway.maps.map.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import wooteco.subway.common.acceptance.AcceptanceTest;
import wooteco.subway.maps.line.acceptance.step.LineAcceptanceStep;
import wooteco.subway.maps.line.dto.LineResponse;
import wooteco.subway.maps.map.acceptance.step.MapAcceptanceStep;
import wooteco.subway.maps.station.acceptance.step.StationAcceptanceStep;
import wooteco.subway.maps.station.dto.StationResponse;

import java.util.Arrays;

import static org.hamcrest.Matchers.notNullValue;
import static wooteco.subway.maps.line.acceptance.step.LineStationAcceptanceStep.지하철_노선에_지하철역_등록되어_있음;

@DisplayName("지하철 노선에 역 등록 관련 기능")
public class MapAcceptanceTest extends AcceptanceTest {
    private Long 교대역;
    private Long 강남역;
    private Long 양재역;
    private Long 남부터미널역;
    private Long 이호선;
    private Long 신분당선;
    private Long 삼호선;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        교대역 = 지하철역_등록되어_있음("교대역");
        강남역 = 지하철역_등록되어_있음("강남역");
        양재역 = 지하철역_등록되어_있음("양재역");
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역");

        이호선 = 지하철_노선_등록되어_있음("2호선", "GREEN", "0");
        신분당선 = 지하철_노선_등록되어_있음("신분당선", "RED", "900");
        삼호선 = 지하철_노선_등록되어_있음("3호선", "ORANGE", "500");

        지하철_노선에_지하철역_등록되어_있음(이호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(이호선, 교대역, 강남역, 2, 2);

        지하철_노선에_지하철역_등록되어_있음(신분당선, null, 강남역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(신분당선, 강남역, 양재역, 2, 1);

        지하철_노선에_지하철역_등록되어_있음(삼호선, null, 교대역, 0, 0);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 1, 2);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 남부터미널역, 양재역, 2, 2);
    }

    @DisplayName("지하철 노선도를 조회한다.")
    @Test
    void loadMap() {
        // when
        ExtractableResponse<Response> response = MapAcceptanceStep.지하철_노선도_조회_요청();

        // then
        MapAcceptanceStep.지하철_노선도_응답됨(response);
        MapAcceptanceStep.지하철_노선도_노선별_지하철역_순서_정렬됨(response, 이호선, Arrays.asList(교대역, 강남역));
        MapAcceptanceStep.지하철_노선도_노선별_지하철역_순서_정렬됨(response, 신분당선, Arrays.asList(강남역, 양재역));
        MapAcceptanceStep.지하철_노선도_노선별_지하철역_순서_정렬됨(response, 삼호선, Arrays.asList(교대역, 남부터미널역, 양재역));
    }

    @DisplayName("캐시 적용을 검증한다.")
    @Test
    void loadMapWithETag() {
        ExtractableResponse<Response> response = MapAcceptanceStep.지하철_노선도_조회_요청();

        String eTag = response.header("ETag");
        RestAssured.given().log().all().
                header("If-None-Match", eTag).
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/maps").
                then().
                statusCode(HttpStatus.NOT_MODIFIED.value()).
                header("ETag", notNullValue()).
                log().all().
                extract();
    }

    private Long 지하철_노선_등록되어_있음(String name, String color, String extraFare) {
        ExtractableResponse<Response> createLineResponse1 = LineAcceptanceStep.지하철_노선_등록되어_있음(name, color, extraFare);
        return createLineResponse1.as(LineResponse.class).getId();
    }

    private Long 지하철역_등록되어_있음(String name) {
        ExtractableResponse<Response> createdStationResponse1 = StationAcceptanceStep.지하철역_등록되어_있음(name);
        return createdStationResponse1.as(StationResponse.class).getId();
    }
}
