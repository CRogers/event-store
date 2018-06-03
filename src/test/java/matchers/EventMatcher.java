package matchers;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;
import uk.callumr.eventstore.core.EventType;
import uk.callumr.eventstore.core.NewEvent;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.equalTo;

public enum EventMatcher {
    ;

    public static class EventEntityIdMatcher extends FeatureMatcher<Event, EntityId> {
        public EventEntityIdMatcher(Matcher<EntityId> subMatcher) {
            super(subMatcher, "entityId", "entityId");
        }

        @Override
        protected EntityId featureValueOf(Event actual) {
            return actual.entityId();
        }
    }

    public static class EventDataMatcher extends FeatureMatcher<Event, String> {
        public EventDataMatcher(Matcher<String> subMatcher) {
            super(subMatcher, "data", "data");
        }

        @Override
        protected String featureValueOf(Event actual) {
            return actual.data();
        }
    }

    public static class EventTypeMatcher extends FeatureMatcher<Event, EventType> {
        public EventTypeMatcher(Matcher<EventType> subMatcher) {
            super(subMatcher, "eventType", "eventType");
        }

        @Override
        protected EventType featureValueOf(Event actual) {
            return actual.eventType();
        }
    }

    public static Matcher<Event> matchingEvent(EntityId entityId, NewEvent newEvent) {
        return both(new EventEntityIdMatcher(equalTo(entityId)))
                .and(new EventTypeMatcher(equalTo(newEvent.eventType())))
                .and(new EventDataMatcher(equalTo(newEvent.data())));
    }

}
