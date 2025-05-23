package io.github.buzzxu.spuddy.util;


import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

import static com.google.common.base.Preconditions.*;

/**
 * @program: 
 * @description:
 * @author: 徐翔
 * @create: 2018-08-01 21:18
 **/
public final class HostAndPort implements Serializable { /** Magic value indicating the absence of a port number. */

    private static final int NO_PORT = -1;
    private static final long serialVersionUID = 4707061337369195944L;

    /** Hostname, IPv4/IPv6 literal, or unvalidated nonsense. */
    private final String host;

    /** Validated port number in the range [0..65535], or NO_PORT */
    private final int port;

    /** True if the parsed host has colons, but no surrounding brackets. */
    private final boolean hasBracketlessColons;

    private HostAndPort(String host, int port, boolean hasBracketlessColons) {
        this.host = host;
        this.port = port;
        this.hasBracketlessColons = hasBracketlessColons;
    }

    /**
     * Returns the portion of this {@code HostAndPort} instance that should represent the hostname or
     * IPv4/IPv6 literal.
     *
     * <p>A successful parse does not imply any degree of sanity in this field. For additional
     * validation, see the {HostSpecifier} class.
     *
     * @since 20.0 (since 10.0 as {@code getHostText})
     */
    public String getHost() {
        return host;
    }



    /** Return true if this instance has a defined port. */
    public boolean hasPort() {
        return port >= 0;
    }

    /**
     * Get the current port number, failing if no port is defined.
     *
     * @return a validated port number, in the range [0..65535]
     * @throws IllegalStateException if no port is defined. You can use {@link #withDefaultPort(int)}
     *     to prevent this from occurring.
     */
    public int getPort() {
        checkState(hasPort(), "needs port");
        return port;
    }

    /**
     * Returns the current port number, with a default if no port is defined.
     */
    public int getPortOrDefault(int defaultPort) {
        return hasPort() ? port : defaultPort;
    }

    /**
     * Build a HostAndPort instance from separate host and port values.
     *
     * <p>Note: Non-bracketed IPv6 literals are allowed. Use {@link #requireBracketsForIPv6()} to
     * prohibit these.
     *
     * @param host the host string to parse. Must not contain a port number.
     * @param port a port number from [0..65535]
     * @return if parsing was successful, a populated HostAndPort object.
     * @throws IllegalArgumentException if {@code host} contains a port number, or {@code port} is out
     *     of range.
     */
    public static HostAndPort fromParts(String host, int port) {
        checkArgument(isValidPort(port));
        HostAndPort parsedHost = fromString(host);
        checkArgument(!parsedHost.hasPort());
        return new HostAndPort(parsedHost.host, port, parsedHost.hasBracketlessColons);
    }

    /**
     * Build a HostAndPort instance from a host only.
     *
     * <p>Note: Non-bracketed IPv6 literals are allowed. Use {@link #requireBracketsForIPv6()} to
     * prohibit these.
     *
     * @param host the host-only string to parse. Must not contain a port number.
     * @return if parsing was successful, a populated HostAndPort object.
     * @throws IllegalArgumentException if {@code host} contains a port number.
     * @since 17.0
     */
    public static HostAndPort fromHost(String host) {
        HostAndPort parsedHost = fromString(host);
        checkArgument(!parsedHost.hasPort());
        return parsedHost;
    }

    /**
     * Split a freeform string into a host and port, without strict validation.
     *
     * Note that the host-only formats will leave the port field undefined. You can use
     * {@link #withDefaultPort(int)} to patch in a default value.
     *
     * @param hostPortString the input string to parse.
     * @return if parsing was successful, a populated HostAndPort object.
     * @throws IllegalArgumentException if nothing meaningful could be parsed.
     */
    public static HostAndPort fromString(String hostPortString) {
        checkNotNull(hostPortString);
        String host;
        String portString = null;
        boolean hasBracketlessColons = false;

        if (hostPortString.startsWith("[")) {
            String[] hostAndPort = getHostAndPortFromBracketedHost(hostPortString);
            host = hostAndPort[0];
            portString = hostAndPort[1];
        } else {
            int colonPos = hostPortString.indexOf(':');
            if (colonPos >= 0 && hostPortString.indexOf(':', colonPos + 1) == -1) {
                // Exactly 1 colon. Split into host:port.
                host = hostPortString.substring(0, colonPos);
                portString = hostPortString.substring(colonPos + 1);
            } else {
                // 0 or 2+ colons. Bare hostname or IPv6 literal.
                host = hostPortString;
                hasBracketlessColons = (colonPos >= 0);
            }
        }

        int port = NO_PORT;
        if (!Strings.isNullOrEmpty(portString)) {
            // Try to parse the whole port string as a number.
            // JDK7 accepts leading plus signs. We don't want to.
            checkArgument(!portString.startsWith("+"));
            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unparseable port number: " + hostPortString);
            }
            checkArgument(isValidPort(port));
        }

        return new HostAndPort(host, port, hasBracketlessColons);
    }

    /**
     * Parses a bracketed host-port string, throwing IllegalArgumentException if parsing fails.
     *
     * @param hostPortString the full bracketed host-port specification. Post might not be specified.
     * @return an array with 2 strings: host and port, in that order.
     * @throws IllegalArgumentException if parsing the bracketed host-port string fails.
     */
    private static String[] getHostAndPortFromBracketedHost(String hostPortString) {
        int colonIndex = 0;
        int closeBracketIndex = 0;
        checkArgument(hostPortString.charAt(0) == '[');
        colonIndex = hostPortString.indexOf(':');
        closeBracketIndex = hostPortString.lastIndexOf(']');
        checkArgument(colonIndex > -1 && closeBracketIndex > colonIndex);

        String host = hostPortString.substring(1, closeBracketIndex);
        if (closeBracketIndex + 1 == hostPortString.length()) {
            return new String[] {host, ""};
        } else {
            checkArgument(hostPortString.charAt(closeBracketIndex + 1) == ':');
            for (int i = closeBracketIndex + 2; i < hostPortString.length(); ++i) {
                checkArgument(Character.isDigit(hostPortString.charAt(i)));
            }
            return new String[] {host, hostPortString.substring(closeBracketIndex + 2)};
        }
    }

    /**
     * Provide a default port if the parsed string contained only a host.
     *
     * You can chain this after {@link #fromString(String)} to include a port in case the port was
     * omitted from the input string. If a port was already provided, then this method is a no-op.
     *
     * @param defaultPort a port number, from [0..65535]
     * @return a HostAndPort instance, guaranteed to have a defined port.
     */
    public HostAndPort withDefaultPort(int defaultPort) {
        checkArgument(isValidPort(defaultPort));
        if (hasPort() || port == defaultPort) {
            return this;
        }
        return new HostAndPort(host, defaultPort, hasBracketlessColons);
    }

    /**
     * Generate an error if the host might be a non-bracketed IPv6 literal.
     *
     * <p>URI formatting requires that IPv6 literals be surrounded by brackets, like "[2001:db8::1]".
     * Chain this call after {@link #fromString(String)} to increase the strictness of the parser, and
     * disallow IPv6 literals that don't contain these brackets.
     *
     * <p>Note that this parser identifies IPv6 literals solely based on the presence of a colon. To
     * perform actual validation of IP addresses, see the {InetAddresses#forString(String)}
     * method.
     *
     * @return {@code this}, to enable chaining of calls.
     * @throws IllegalArgumentException if bracketless IPv6 is detected.
     */
    public HostAndPort requireBracketsForIPv6() {
        checkArgument(!hasBracketlessColons);
        return this;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof HostAndPort) {
            HostAndPort that = (HostAndPort) other;
            return StringUtils.equalsIgnoreCase(this.host, that.host)
                    && this.port == that.port
                    && this.hasBracketlessColons == that.hasBracketlessColons;
        }
        return false;
    }

    @Override
    public int hashCode() {

        return Arrays.hashCode(new Object[]{host, port, hasBracketlessColons});
    }

    /** Rebuild the host:port string, including brackets if necessary. */
    @Override
    public String toString() {
        // "[]:12345" requires 8 extra bytes.
        StringBuilder builder = new StringBuilder(host.length() + 8);
        if (host.indexOf(':') >= 0) {
            builder.append('[').append(host).append(']');
        } else {
            builder.append(host);
        }
        if (hasPort()) {
            builder.append(':').append(port);
        }
        return builder.toString();
    }

    /** Return true for valid port numbers. */
    private static boolean isValidPort(int port) {
        return port >= 0 && port <= 65535;
    }

}
