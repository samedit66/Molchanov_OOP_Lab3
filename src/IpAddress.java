import java.util.Arrays;

public class IpAddress {
    private static final int OCTETS_COUNT = 4;
    private static final int OCTET_LENGTH = 8;
    private static final int MIN_OCTET_VALUE = 0;
    private static final int MAX_OCTET_VALUE = 255;

    private static final int MIN_MASK_LENGTH = 1;
    private static final int MAX_MASK_LENGTH = 32;

    private final int[] _rawAddress;

    public IpAddress(int[] rawAddress) {
        checkAddress(rawAddress);
        this._rawAddress = rawAddress;
    }

    public IpAddress(int maskLength) {
        // Длина маски должна быть целым число в пределах от 1 до 32 включительно
        if (maskLength < MIN_MASK_LENGTH || maskLength > MAX_MASK_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Mask length must be in range [%d..%d]", MIN_MASK_LENGTH, MAX_MASK_LENGTH)
            );
        }
        this._rawAddress = createRawMask(maskLength);
    }

    public static IpAddress localHost() {
        return new IpAddress(new int[]{ 127, 0, 0, 1 });
    }

    public static IpAddress defaultRoute() {
        return new IpAddress(new int[]{ 0, 0, 0, 0 });
    }

    public static IpAddress limitedBroadcast() {
        return new IpAddress(new int[]{ 255, 255, 255, 255 });
    }

    /** Применяет маску к IP-адресу и возвращает новый IP-адрес
     *
     */
    public IpAddress applyMask(IpAddress mask) {
        if (mask == null) {
            throw new IllegalArgumentException("Mask cannot be null");
        }

        checkMask(mask.getRawAddress());

        int[] rawMask = mask.getRawAddress();
        int[] maskedAddress = {
                MIN_OCTET_VALUE,
                MIN_OCTET_VALUE,
                MIN_OCTET_VALUE,
                MIN_OCTET_VALUE,
        };

        int[] rawAddress = this.getRawAddress();
        for (int i = 0; i < OCTETS_COUNT; i++) {
            maskedAddress[i] = rawAddress[i] & rawMask[i];
        }

        return new IpAddress(maskedAddress);
    }

    /** Проверяет принадлежность IP-адреса к заданной сети
     *
     */
    public boolean belongsToSubnet(IpAddress subnetAddress, IpAddress mask) {
        checkMask(mask.getRawAddress());
        return this.applyMask(mask).equals(subnetAddress);
    }

    /** Вычисляет количество устройств (хостов) между двумя IP-адресами одной сети (не включая эти адреса)
     *
     */
    public long countHostsBetween(IpAddress other, IpAddress mask) {
        checkMask(mask.getRawAddress());

        // Проверяем, что 2 IP-адреса находятся в одной сети
        IpAddress netAddress = other.applyMask(mask);
        if (!this.belongsToSubnet(netAddress, mask)) {
            throw new IllegalArgumentException("IP-addresses not in the same net");
        }

        if (this.equals(other)) {
            return 0L;
        }

        return Math.abs(this.toLong() - other.toLong()) - 1;
    }


    /** Представляет IP-адрес в виде целого числа
     */
    public long toLong() {
        int[] rawAddress = this.getRawAddress();
        long rawAddressAsInt = 0;

        for (int i = OCTETS_COUNT - 1; i >= 0; i--) {
            rawAddressAsInt += (long) rawAddress[OCTETS_COUNT - i - 1] << OCTET_LENGTH * i;
        }

        return rawAddressAsInt;
    }


    /** Создаёт строковое представление IP-адреса
     *
     */
    @Override
    public String toString() {
        return String.format("%d.%d.%d.%d", this._rawAddress[0], this._rawAddress[1],
                this._rawAddress[2], this._rawAddress[3]);
    }


    /** Сравнивает два IP-адрес
     *
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof IpAddress other)) {
            return false;
        }

        return Arrays.equals(this.getRawAddress(), other.getRawAddress());
    }


    /** Возвращает "сырой" IP-адрес в виде массиа чисел
     *
     */
    public int[] getRawAddress() {
        return this._rawAddress.clone();
    }


    /** Проверяет, что массив целых чисел может быть правильной записью IP-адреса
     *
     */
    private void checkAddress(int[] rawAddress) {
        // Массив чисел нельзя представить в виде IP-адреса, если он не состоит из ровно четырех целых чисел
        if (rawAddress.length != OCTETS_COUNT) {
            throw new IllegalArgumentException(
                    String.format("IP address must consist only of %d octets", OCTETS_COUNT)
            );
        }

        // Или какое-либо из этих чисел лежит не в пределах от 0 до 255 включительно
        for (int octet : rawAddress) {
            if (octet < MIN_OCTET_VALUE || octet > MAX_OCTET_VALUE) {
                throw new IllegalArgumentException(
                        String.format("Each octet in IP address must be in range [%d..%d]",
                                MIN_OCTET_VALUE, MAX_OCTET_VALUE)
                );
            }
        }
    }


    /** Проверяет, что массив целых чисел может быть правильной записью маски
     *
     */
    private void checkMask(int[] rawMask) {
        boolean expectOnlyZeros = false;

        for (int octet : rawMask) {
            // Если уже были пройдены все октеты, состоящие лишь из единиц, и, возможно,
            // "смешанного" октета, состоящего сначала из единиц, а потом только из нулей,
            // то далее в маске ожидаются лишь нулевые октеты
            if (expectOnlyZeros && octet != MIN_OCTET_VALUE) {
                throw new IllegalArgumentException("Invalid mask");
            }

            // В начале маски ожидаются только октеты, состоящие из единиц
            if (octet == MAX_OCTET_VALUE) {
                continue;
            }

            // Октет идущий после октетов с единицами (если они были),
            // должен сначала состоять из одниц единиц...
            while (octet != 0 && (octet & 0x1) == 0) {
                octet >>= 1;
            }

            // ... а потом только из одних нулей
            while (octet != 0) {
                if ((octet & 0x1) != 1) {
                    throw new IllegalArgumentException("Invalid mask");
                }

                octet >>= 1;
            }

            // Все ненулевые октеты были пройдены, далее в маске должны идти только нули
            expectOnlyZeros = true;
        }
    }


    /** Создаёт "сырую" маску в виде массива байтов
     *
     */
    private int[] createRawMask(int maskLength) {
        int[] rawMask = {
                MAX_OCTET_VALUE,
                MAX_OCTET_VALUE,
                MAX_OCTET_VALUE,
                MAX_OCTET_VALUE,
        };

        // Указывает на индекс октета идущего сразу же после октета, состоящего из одних единиц
        int currentOctetIdx = maskLength / OCTET_LENGTH;

        // Если маска не состоит полностью из единиц...
        if (currentOctetIdx != OCTETS_COUNT) {
            // Определяем, насколько нужно сдвинуть влево октет mask[currentOctetIdx]
            int shift = OCTET_LENGTH - (maskLength % OCTET_LENGTH);

            // Обнуляем биты у нужного октета
            rawMask[currentOctetIdx] &= rawMask[currentOctetIdx] << shift;

            // Все октеты, идущие после, уставливаем в ноль
            for (currentOctetIdx++; currentOctetIdx < OCTETS_COUNT; currentOctetIdx++) {
                rawMask[currentOctetIdx] = MIN_OCTET_VALUE;
            }
        }

        return rawMask;
    }
}
