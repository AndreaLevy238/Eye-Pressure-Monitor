package io.github.andrealevy238.eyepressuremonitor;

class DataConverter {

    static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * @param bytes a byte array that specifies some integer
     * @return an iteger represented by the cur
     */
    static int getNum(byte[] bytes) {
        return bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8;
    }


}
