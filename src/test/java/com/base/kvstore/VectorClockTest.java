package com.base.kvstore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class VectorClockTest {
    private VectorClock clock1;
    private VectorClock clock2;

    private void increment(VectorClock clock, String nodeId, int count) {
        for (int i = 0; i < count; i++) {
            clock.increments(nodeId);
        }
    }

    @BeforeEach
    void setup(){
        clock1 = new VectorClock();
        clock2 = new VectorClock();
    }

    @Test
    @DisplayName("Should increment clock for specific node")
    void shouldIncrementClock() {
        clock1.increments("node1");
        assertThat(clock1.getClock()).containsEntry("node1", 1);

        clock1.increments("node1");
        assertThat(clock1.getClock()).containsEntry("node1",2);

        clock1.increments("node2");
        assertThat(clock1.getClock())
                .containsEntry("node1",2)
                .containsEntry("node2", 1);
    }

    @Test
    @DisplayName("Should merge two vector clocks correctly")
    void shouldMergeClocks() {
        increment(clock1, "node1",2);
        increment(clock2, "node2", 3);

        clock1.merge(clock2);

        assertThat(clock1.getClock())
                .containsEntry("node1", 2)
                .containsEntry("node2",3);


    }

    @Test
    @DisplayName("Should merge with overlapping nodes")
    void shouldMergeWithOverlappingNodes() {
        increment(clock1, "node1", 3);
        increment(clock2, "node1", 5);
        increment(clock2, "node2", 2);

        clock1.merge(clock2);

        assertThat(clock1.getClock())
                .containsEntry("node1", 5)
                .containsEntry("node2",2);
    }

    @Test
    @DisplayName("Should detect BEFORE relationship")
    void shouldDetectBeforeRelationship() {
        // clock1: {node1: 2, node2: 1}
        // clock2: {node1: 3, node2: 2}

        increment(clock1, "node1", 2);
        clock1.increments("node2");
        increment(clock2, "node1", 3);
        increment(clock2, "node2", 2);

        assertThat(clock1.compare(clock2))
                .isEqualTo(ComparisonResult.BEFORE);

    }

    @Test
    @DisplayName("Should detect AFTER relationship")
    void shouldDetectAfterRelationship() {
        // clock1: {node1: 3, node2: 2}
        // clock2: {node1: 2, node2: 1}

        increment(clock1, "node1", 3);
        increment(clock1, "node2", 2);
        increment(clock2, "node1", 2);
        increment(clock2, "node2", 1);

        assertThat(clock1.compare(clock2))
                .isEqualTo(ComparisonResult.AFTER);

    }

    @Test
    @DisplayName("Should detect CONCURRENT relationship")
    void shouldDetectConcurrentRelationship() {
        // clock1: {node1: 3, node2: 1}
        // clock2: {node1: 2, node2: 2}

        increment(clock1, "node1", 3);
        increment(clock1, "node2", 1);
        increment(clock2, "node1", 2);
        increment(clock2, "node2", 2);

        assertThat(clock1.compare(clock2))
                .isEqualTo(ComparisonResult.CONCURRENT);

    }

    @Test
    @DisplayName("Should detect EQUAL relationship")
    void shouldDetectEqualRelationship() {
        increment(clock1, "node1", 2);
        increment(clock2, "node1", 2);
        assertThat(clock1.compare(clock2))
                .isEqualTo(ComparisonResult.EQUAL);
    }


    @Test
    @DisplayName("Should handle clocks with different node sets")
    void shouldHandleDifferentNodeSets() {
        increment(clock1, "node1", 2);
        clock2.increments("node2");

        // clock1 has node1, clock2 has node2 - should be concurrent
        assertThat(clock1.compare(clock2))
                .isEqualTo(ComparisonResult.CONCURRENT);
    }

    @Test
    @DisplayName("Should handle empty clocks")
    void shouldHandleEmptyClocks() {
        assertThat(clock1.compare(clock2))
                .isEqualTo(ComparisonResult.EQUAL);
    }


    @Test
    @DisplayName("Should provide immuatable clock map")
    void shouldProvideImmutableClockMap() {
        clock1.increments("node1");
        Map<String, Integer> clockMap = clock1.getClock();
        clockMap.put("node2", 5); // no affect on original
        assertThat(clock1.getClock()).doesNotContainKey("node2");
    }
}
