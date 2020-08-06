package wooteco.subway.maps.map.documentation;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.context.WebApplicationContext;

import wooteco.security.core.TokenResponse;
import wooteco.subway.common.documentation.Documentation;
import wooteco.subway.maps.map.application.MapService;
import wooteco.subway.maps.map.domain.PathType;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.ui.MapController;
import wooteco.subway.maps.station.dto.StationResponse;

@WebMvcTest(controllers = {MapController.class})
public class PathDocumentation extends Documentation {

    @Autowired
    private MapController mapController;

    @MockBean
    private MapService mapService;

    protected TokenResponse tokenResponse;

    @BeforeEach
    public void setUp(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        super.setUp(context, restDocumentation);
        tokenResponse = new TokenResponse("token");
    }

    @Test
    void findPath() {
        final List<StationResponse> StationResponses = Arrays.asList(
                new StationResponse(1L, "교대역", LocalDateTime.now(), LocalDateTime.now()),
                new StationResponse(2L, "강남역", LocalDateTime.now(), LocalDateTime.now()),
                new StationResponse(3L, "양재역", LocalDateTime.now(), LocalDateTime.now())
        );
        final PathResponse pathResponse = new PathResponse(StationResponses, 20, 18, 1250);
        Long source = 1L;
        Long target  = 3L;
        PathType type = PathType.DISTANCE;

        when(mapService.findPath(any(), any(), any())).thenReturn(pathResponse);

        given().log().all().
                header("Authorization", "Bearer " + tokenResponse.getAccessToken()).
                when().
                get("/paths?source={source}&target={target}&type={type}", source, target, type).
                then().
                log().all().
                apply(document("paths/find-path",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("Bearer auth credentials")),
                        requestParameters(
                            parameterWithName("source").description("출발역"),
                            parameterWithName("target").description("도착역"),
                            parameterWithName("type").description("타입")
                        ),
                        responseFields(
                                fieldWithPath("stations.[].id").type(JsonFieldType.NUMBER).description("역의 Id"),
                                fieldWithPath("stations.[].name").type(JsonFieldType.STRING).description("역의 이름"),
                                fieldWithPath("duration").type(JsonFieldType.NUMBER).description("최단 시간"),
                                fieldWithPath("distance").type(JsonFieldType.NUMBER).description("최단 거리"),
                                fieldWithPath("fare").type(JsonFieldType.NUMBER).description("운임 비용"))))
                .extract();
    }
}
