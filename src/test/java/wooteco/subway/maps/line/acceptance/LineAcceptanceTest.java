package wooteco.subway.maps.line.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import wooteco.subway.common.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import wooteco.subway.maps.line.acceptance.step.LineAcceptanceStep;

import java.util.Arrays;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_생성_요청("신분당선", "RED", "900");

        // then
        LineAcceptanceStep.지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        LineAcceptanceStep.지하철_노선_등록되어_있음("신분당선", "bg-red-600", "900");

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_생성_요청("신분당선", "RED", "900");

        // then
        LineAcceptanceStep.지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1 = LineAcceptanceStep.지하철_노선_등록되어_있음("신분당선", "RED", "900");
        ExtractableResponse<Response> createResponse2 = LineAcceptanceStep.지하철_노선_등록되어_있음("2호선", "GREEN", "0");

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_목록_조회_요청();

        // then
        LineAcceptanceStep.지하철_노선_목록_응답됨(response);
        LineAcceptanceStep.지하철_노선_목록_포함됨(response, Arrays.asList(createResponse1, createResponse2));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = LineAcceptanceStep.지하철_노선_등록되어_있음("신분당선", "RED", "900");

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_조회_요청(createResponse);

        // then
        LineAcceptanceStep.지하철_노선_응답됨(response, createResponse);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        String name = "신분당선";
        ExtractableResponse<Response> createResponse = LineAcceptanceStep.지하철_노선_등록되어_있음(name, "RED", "900");

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_수정_요청(createResponse, "구분당선", "BLUE", "0");

        // then
        LineAcceptanceStep.지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = LineAcceptanceStep.지하철_노선_등록되어_있음("신분당선", "RED", "900");

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_제거_요청(createResponse);

        // then
        LineAcceptanceStep.지하철_노선_삭제됨(response);
    }
}
