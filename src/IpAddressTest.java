import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class IpAddressTest {

    @ParameterizedTest
    @MethodSource("testApplyMaskProvider")
    @DisplayName("Определение подсети при помощи маски")
    void testApplyMask(IpAddress address, IpAddress mask, IpAddress maskedAddress) {
        IpAddress addressBefore = new IpAddress(address.getRawAddress());

        assertEquals(address.applyMask(mask), maskedAddress);

        assertEquals(address, addressBefore);
    }

    static Stream<Arguments> testApplyMaskProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(24),
                        new IpAddress(new int[]{ 127, 0, 0, 0 })
                ),
                arguments(
                        new IpAddress(new int[]{ 192, 168, 0, 13 }),
                        new IpAddress(21),
                        new IpAddress(new int[]{ 192, 168, 0, 0 })
                ),
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(new int[]{ 255, 255, 255, 0 }),
                        new IpAddress(new int[]{ 127, 0, 0, 0 })
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testGetRawAddressProvider")
    @DisplayName("Представление IP-адреса в виде массива чисел")
    void testGetRawAddress(IpAddress address, int[] rawAddress) {
        IpAddress addressBefore = new IpAddress(address.getRawAddress());

        assertArrayEquals(address.getRawAddress(), rawAddress);

        assertEquals(address, addressBefore);
    }

    static Stream<Arguments> testGetRawAddressProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new int[]{ 127, 0, 0, 1 }
                ),
                arguments(
                        new IpAddress(new int[]{ 255, 251, 213, 23 }),
                        new int[]{ 255, 251, 213, 23 }
                ),
                arguments(
                        new IpAddress(24),
                        new int[]{ 255, 255, 255, 0 }
                ),
                arguments(
                        new IpAddress(1),
                        new int[]{ 128, 0, 0, 0 }
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testToStringProvider")
    @DisplayName("Строковое представление IP-адреса")
    void testToString(IpAddress address, String strAddress) {
        IpAddress addressBefore = new IpAddress(address.getRawAddress());

        assertEquals(address.toString(), strAddress);

        assertEquals(address, addressBefore);
    }

    static Stream<Arguments> testToStringProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        "127.0.0.1"
                ),
                arguments(
                        new IpAddress(new int[]{ 255, 251, 213, 23 }),
                        "255.251.213.23"
                ),
                arguments(
                        new IpAddress(24),
                        "255.255.255.0"
                ),
                arguments(
                        new IpAddress(1),
                        "128.0.0.0"
                )
        );
    }


    @ParameterizedTest
    @MethodSource("testInvalidIpAddressProvider")
    @DisplayName("Исключение при неправильной записи IP-адреса")
    void testInvalidIpAddress(int[] invalidRawAddress) {
        assertThrows(IllegalArgumentException.class, () -> {
            new IpAddress(invalidRawAddress);
        });
    }

    static Stream<int[]> testInvalidIpAddressProvider() {
        return Stream.of(
                new int[]{ 1023, 12, 12, 34 },
                new int[]{ 23, 12312, 34, 1 },
                new int[]{ 123, 31, 13151, 3 },
                new int[]{ 123, 31, 12, 123412 },
                new int[]{ 123, 124, 231, 214, 421, 421 }
        );
    }

    @ParameterizedTest
    @MethodSource("testInvalidMaskProvider")
    @DisplayName("Исключение при неправильной записи маски")
    void testInvalidMask(int invalidMaskLength) {
        assertThrows(IllegalArgumentException.class, () -> {
            new IpAddress(invalidMaskLength);
        });
    }

    static Stream<Integer> testInvalidMaskProvider() {
        return Stream.of(-4, 33, 0);
    }

    @ParameterizedTest
    @MethodSource("testInvalidMaskInApplyMaskProvider")
    @DisplayName("Неправильная запись маски при маскировании адреса")
    void testInvalidMaskInCalculateSubnet(IpAddress address, IpAddress addressPretendingToBeMask) {
        assertThrows(IllegalArgumentException.class, () -> {
            address.applyMask(addressPretendingToBeMask);
        });
    }

    static Stream<Arguments> testInvalidMaskInApplyMaskProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(new int[]{ 123, 121, 0, 126 })
                ),
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(new int[]{ 0, 0, 123, 55 })
                ),
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testEqualsProvider")
    @DisplayName("IP-адреса совпадают")
    void testAddressEquals(IpAddress first, IpAddress second) {
        assertEquals(first, second);
    }

    static Stream<Arguments> testEqualsProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(new int[]{ 127, 0, 0, 1 })
                ),
                arguments(
                        new IpAddress(25),
                        new IpAddress(25)
                ),
                arguments(
                        new IpAddress(new int[]{ 255, 255, 255, 0 }),
                        new IpAddress(24)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testNotEqualsProvider")
    @DisplayName("IP-адреса не совпадают")
    void testAddressNotEquals(IpAddress first, IpAddress second) {
        assertNotEquals(first, second);
    }

    static Stream<Arguments> testNotEqualsProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(new int[]{ 255, 245, 0, 1 })
                ),
                arguments(
                        new IpAddress(25),
                        new IpAddress(new int[]{ 127, 0, 0, 1 })
                ),
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        null
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testIpAddressBelongsToSubnetProvider")
    @DisplayName("IP-адрес принадлежит подсети")
    void testIpAddressBelongsToSubnet(IpAddress address, IpAddress subnetAddress, IpAddress mask) {
        IpAddress addressBefore = new IpAddress(address.getRawAddress());

        assertTrue(address.belongsToSubnet(subnetAddress, mask));

        assertEquals(address, addressBefore);
    }

    static Stream<Arguments> testIpAddressBelongsToSubnetProvider() {
        return Stream.of(
                arguments(new IpAddress(new int[]{ 127, 1, 0, 10 }),
                          new IpAddress(new int[]{ 127, 1, 0, 0 }),
                          new IpAddress(24)),
                arguments(new IpAddress(new int[]{ 176, 132, 45, 10 }),
                          new IpAddress(new int[]{ 176, 0, 0, 0 }),
                          new IpAddress(8)),
                arguments(new IpAddress(new int[]{ 176, 0, 0, 0 }),
                          new IpAddress(new int[]{ 176, 0, 0, 0 }),
                          new IpAddress(8))
        );
    }

    @ParameterizedTest
    @MethodSource("testIpAddressDoesNotBelongToSubnetProvider")
    @DisplayName("IP-адрес не принадлежит подсети")
    void testIpAddressDoesNotBelongToSubnet(IpAddress address, IpAddress subnetAddress, IpAddress mask) {
        IpAddress addressBefore = new IpAddress(address.getRawAddress());

        assertFalse(address.belongsToSubnet(subnetAddress, mask));

        assertEquals(address, addressBefore);
    }
    static Stream<Arguments> testIpAddressDoesNotBelongToSubnetProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 2, 0, 10 }),
                        new IpAddress(new int[]{ 127, 1, 0, 0 }),
                        new IpAddress(16)),
                arguments(
                        new IpAddress(new int[]{ 176, 132, 45, 10 }),
                        new IpAddress(new int[]{ 176, 0, 0, 0 }),
                        new IpAddress(16)),
                arguments(
                        new IpAddress(new int[]{ 173, 0, 0, 1 }),
                        new IpAddress(new int[]{ 176, 0, 0, 0 }),
                        new IpAddress(8))
        );
    }

    @ParameterizedTest
    @MethodSource("testToLongProvider")
    @DisplayName("Числовое представление IP-адреса")
    void testToLong(IpAddress address, long addressAsInt) {
        IpAddress addressBefore = new IpAddress(address.getRawAddress());

        assertEquals(address.toLong(), addressAsInt);

        assertEquals(address, addressBefore);
    }

    static Stream<Arguments> testToLongProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 188, 233, 29, 4 }),
                        3169393924L
                ),
                arguments(
                        new IpAddress(new int[]{ 0, 0, 0, 0 }),
                        0L
                ),
                arguments(
                        new IpAddress(new int[]{ 255, 255, 255, 255 }),
                        4294967295L
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testCountHostsBetweenProvider")
    @DisplayName("Подсчет устройств (хостов) между двумя IP-адресами")
    void testCountHostsBetween(IpAddress first, IpAddress second, IpAddress subnetMask, long hostsCount) {
        IpAddress firstBefore = new IpAddress(first.getRawAddress());

        assertEquals(first.countHostsBetween(second, subnetMask), hostsCount);

        assertEquals(first, firstBefore);
    }

    static Stream<Arguments> testCountHostsBetweenProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(new int[]{ 127, 0, 0, 10 }),
                        new IpAddress(24),
                        8L
                ),
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 0 }),
                        new IpAddress(new int[]{ 127, 0, 0, 255 }),
                        new IpAddress(24),
                        254L
                ),
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 0 }),
                        new IpAddress(new int[]{ 127, 0, 0, 0 }),
                        new IpAddress(24),
                        0L
                ),
                arguments(
                        new IpAddress(new int[]{ 184, 5, 0, 134 }),
                        new IpAddress(new int[]{ 184, 5, 146, 0 }),
                        new IpAddress(16),
                        37241L
                )
        );
    }

    @ParameterizedTest
    @MethodSource("testCountHostsWhenIpsNotInTheSameSubnetProvider")
    @DisplayName("Исключение при подсчете устройств, когда IP-адреса находятся не в одной сети")
    void testCountHostsWhenIpsNotInTheSameSubnet(IpAddress first, IpAddress second, IpAddress subnetMask) {
        IpAddress firstBefore = new IpAddress(first.getRawAddress());

        assertThrows(IllegalArgumentException.class, () -> {
            first.countHostsBetween(second, subnetMask);
        });

        assertEquals(first, firstBefore);
    }

    static Stream<Arguments> testCountHostsWhenIpsNotInTheSameSubnetProvider() {
        return Stream.of(
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 1 }),
                        new IpAddress(new int[]{ 127, 1, 0, 10 }),
                        new IpAddress(24),
                        8
                ),
                arguments(
                        new IpAddress(new int[]{ 127, 0, 0, 0 }),
                        new IpAddress(new int[]{ 127, 0, 24, 255 }),
                        new IpAddress(24),
                        254
                )
        );
    }
}