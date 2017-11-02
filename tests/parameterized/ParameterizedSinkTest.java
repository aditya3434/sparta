import sparta.checkers.quals.*;
import sparta.checkers.quals.FlowPermission;
import static sparta.checkers.quals.FlowPermissionString.*;


class ParameterizedSinkTest {
    void sendData(@Source({}) @Sink(INTERNET+"(maps.google.com, voice.google.com, google.com)") Object p) {
        // Allowed: fewer sinks
        noComm(p);
        // Forbidden: less restrictive sink params
        // :: error: (argument.type.incompatible)
        two(p);
    }

    void noComm(@Source({}) @Sink({}) Object p) {
        // Forbidden: more sinks
        // :: error: (argument.type.incompatible)
        sendData(p);
    }

    void two(@Source({}) @Sink({WRITE_SMS, INTERNET+"(*.google.com,google.com)"}) Object p) {
        // Allowed: fewer sinks, more restrictive sink params
        sendData(p);
        // Forbidden: more sinks
        // :: error: (argument.type.incompatible)
        any(p);
    }

    void any(@Source({}) @Sink(ANY) Object p) {
        // Allowed: fewer sinks
        two(p);
    }

    // Null is legal for all sinks.
    void testNull() {
        any(null);
        two(null);
    }
}