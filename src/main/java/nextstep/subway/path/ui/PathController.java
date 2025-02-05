package nextstep.subway.path.ui;

import nextstep.subway.auth.domain.AuthMember;
import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> findShortestPath(@AuthenticationPrincipal(required = false) AuthMember member,
                                                         @RequestParam(name = "source") Long source,
                                                         @RequestParam(name = "target") Long target) {
        return ResponseEntity.ok(pathService.findShortestPath(source, target, member.getAge()));
    }

}
