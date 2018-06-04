package matchers;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.VersionedEvent;
import uk.callumr.eventstore.core.EventType;
import uk.callumr.eventstore.core.Event;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;

public enum EventMatcher {
    ;

    public static class EventEntityIdMatcher extends FeatureMatcher<VersionedEvent, EntityId> {
        public EventEntityIdMatcher(Matcher<EntityId> subMatcher) {
            super(subMatcher, "entityId", "entityId");
        }

        @Override
        protected EntityId featureValueOf(VersionedEvent actual) {
            return actual.entityId();
        }
    }

    public static class EventDataMatcher extends FeatureMatcher<VersionedEvent, String> {
        public EventDataMatcher(Matcher<String> subMatcher) {
            super(subMatcher, "data", "data");
        }

        @Override
        protected String featureValueOf(VersionedEvent actual) {
            return actual.data();
        }
    }

    public static class EventTypeMatcher extends FeatureMatcher<VersionedEvent, EventType> {
        public EventTypeMatcher(Matcher<EventType> subMatcher) {
            super(subMatcher, "eventType", "eventType");
        }

        @Override
        protected EventType featureValueOf(VersionedEvent actual) {
            return actual.eventType();
        }
    }

    public static Matcher<VersionedEvent> matchingEvent(EntityId entityId, Event event) {
        return both(new EventEntityIdMatcher(equalTo(entityId)))
                .and(new EventTypeMatcher(equalTo(event.eventType())))
                .and(new EventDataMatcher(equalTo(event.data())));
    }

}
