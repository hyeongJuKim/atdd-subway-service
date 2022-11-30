package nextstep.subway.path.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PathFinderTest {
    private Line 신분당선;
    private Line 이호선;
    private Line 삼호선;
    private Station 강남역;
    private Station 양재역;
    private Station 교대역;
    private Station 남부터미널역;

    /**
     * 교대역       --- *2호선(10)* ---   강남역
     * |                                    |
     * *3호선(3)*                       *신분당선(10)*
     * |                                    |
     * 남부터미널역  --- *3호선(2)* ---     양재
     */
    @BeforeEach
    public void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        교대역 = new Station("교대역");
        남부터미널역 = new Station("남부터미널역");
        신분당선 = new Line("신분당선", "bg-red-600", 강남역, 양재역, 10);
        이호선 = new Line("이호선", "bg-red-600", 교대역, 강남역, 10);
        삼호선 = new Line("삼호선", "bg-red-600", 교대역, 양재역, 5);
    }

    @DisplayName("출발역과 도착역 최단 경로 조회")
    @Test
    void findShortestPath() {
        PathFinder pathFinder = PathFinder.from(Arrays.asList(삼호선, 이호선));
        Path path = pathFinder.findShortestPath(교대역, 양재역);

        assertAll(
                () -> assertThat(path.getStations()).hasSize(2),
                () -> assertThat(path.getDistance()).isEqualTo(5)
        );
    }
}
