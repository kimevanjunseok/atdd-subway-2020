package wooteco.subway.maps.map.domain;

import java.util.Arrays;
import java.util.function.Function;

public enum AgeType {

    BABY(6, fare -> 0),
    CHILD(13, fare -> (int)((fare - 350) * 0.8)),
    YOUTH(19, fare -> (int)((fare - 350) * 0.5)),
    ADULT(1000, fare -> fare);

    private final int maxAge;
    private final Function<Integer, Integer> finalFare;

    AgeType(int maxAge, Function<Integer, Integer> finalFare) {
        this.maxAge = maxAge;
        this.finalFare = finalFare;
    }

    public static AgeType find(int age) {
        return Arrays.stream(AgeType.values())
                .filter(ageType -> ageType.maxAge < age)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public int apply(int fare) {
        return finalFare.apply(fare);
    }
}
