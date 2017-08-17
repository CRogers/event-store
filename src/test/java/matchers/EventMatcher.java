package matchers;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import uk.callumr.eventstore.core.EntityId;
import uk.callumr.eventstore.core.Event;

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
            super(subMatcher, "eventData", "eventData");
        }

        @Override
        protected String featureValueOf(Event actual) {
            return actual.eventData();
        }
    }

    public static Matcher<Event> matchingEvent(EntityId entityId, String eventData) {
        return both(new EventEntityIdMatcher(equalTo(entityId))).and(
                new EventDataMatcher(equalTo(eventData))
        );
    }

}
