package com.careerradar.eligibility;

import com.careerradar.job.domain.ForeignRegion;
import com.careerradar.job.domain.GeographyType;
import com.careerradar.job.domain.WorkplaceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocationNormalizerTest {

    private final LocationNormalizer normalizer =
            new LocationNormalizer();

    @Test
    void shouldNormalizeRemoteUkAndIreland() {

        LocationNormalizer.NormalizedLocation result =
                normalizer.normalize(
                        "Remote-UK&I",
                        "Remote-UK&I",
                        "Remote-UK&I"
                );

        assertEquals(
                GeographyType.FOREIGN,
                result.geographyType()
        );

        assertEquals(
                ForeignRegion.UK,
                result.foreignRegion()
        );

        assertEquals(
                WorkplaceType.REMOTE,
                result.workplaceType()
        );
    }

    @Test
    void shouldNormalizeRemoteEurope() {

        LocationNormalizer.NormalizedLocation result =
                normalizer.normalize(
                        "Remote-Northern & Southern Europe",
                        "Remote-Northern & Southern Europe",
                        "Remote-Northern & Southern Europe"
                );

        assertEquals(
                GeographyType.FOREIGN,
                result.geographyType()
        );

        assertEquals(
                ForeignRegion.EUROPE,
                result.foreignRegion()
        );

        assertEquals(
                WorkplaceType.REMOTE,
                result.workplaceType()
        );
    }

    @Test
    void shouldNormalizeRemoteAmer() {

        LocationNormalizer.NormalizedLocation result =
                normalizer.normalize(
                        "Remote-AMER",
                        "Remote-AMER",
                        "Remote-AMER"
                );

        assertEquals(
                GeographyType.FOREIGN,
                result.geographyType()
        );

        assertEquals(
                ForeignRegion.USA,
                result.foreignRegion()
        );

        assertEquals(
                WorkplaceType.REMOTE,
                result.workplaceType()
        );
    }

    @Test
    void shouldNormalizeIndia() {

        LocationNormalizer.NormalizedLocation result =
                normalizer.normalize(
                        "Bengaluru, India",
                        "India",
                        "Bengaluru"
                );

        assertEquals(
                GeographyType.INDIA,
                result.geographyType()
        );
    }

    @Test
    void shouldRemainUnknownWhenWorkplaceIsNotSpecified() {

        LocationNormalizer.NormalizedLocation result =
                normalizer.normalize(
                        "Argentina",
                        "Argentina",
                        "Argentina"
                );

        assertEquals(
                GeographyType.FOREIGN,
                result.geographyType()
        );

        assertEquals(
                WorkplaceType.UNKNOWN,
                result.workplaceType()
        );
    }
}