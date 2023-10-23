package com.crabcode.factory.data.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataIn {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private ByteArrayInputStream in;
    public DataIn() {
        this.in = new ByteArrayInputStream(EMPTY_BYTE_ARRAY);
    }

    public void close() {
        this.in = new ByteArrayInputStream(EMPTY_BYTE_ARRAY);
    }

    public void reset(byte[] data) {
        this.in = new ByteArrayInputStream(data);
    }

    public int remaining() {
        return this.in.available();
    }

    public boolean readBoolean() {
        this.checkAvailable();
        return this.in.read() == 1;
    }

    public byte[] readBytes() {
        int available = this.in.available();
        if (available == 0) {
            return EMPTY_BYTE_ARRAY;
        } else {
            this.checkAvailable();
            byte[] b = new byte[available];

            try {
                this.in.read(b);
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            return b;
        }
    }

    public byte[] readBytes(int length) {
        if (length == 0) {
            return EMPTY_BYTE_ARRAY;
        } else {
            this.checkAvailable();
            byte[] b = new byte[length];

            try {
                this.in.read(b);
            } catch (IOException var4) {
                var4.printStackTrace();
            }

            return b;
        }
    }

    public String readString() {
        this.checkAvailable();
        int length = this.readInt();
        byte[] data = this.readBytes(length);
        return new String(data, StandardCharsets.UTF_8);
    }

    public short readSignedShort() {
        this.checkAvailable();
        byte[] stufs = new byte[2];

        try {
            this.in.read(stufs);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return ByteBuffer.wrap(stufs).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    public int readUnsignedShort() {
        return (this.in.read() << 8) + this.in.read();
    }

    public int readInt() {
        this.checkAvailable();
        int ch1 = this.in.read();
        int ch2 = this.in.read();
        int ch3 = this.in.read();
        int ch4 = this.in.read();
        return (ch1 | ch2 | ch3 | ch4) < 0 ? 0 : (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }

    public long readLong() {
        this.checkAvailable();
        byte[] stufs = new byte[8];

        try {
            this.in.read(stufs);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return ByteBuffer.wrap(stufs).order(ByteOrder.BIG_ENDIAN).getLong();
    }

    public int readVarInt() {
        this.checkAvailable();
        int i = 0;
        int j = 0;

        byte b0;
        try {
            do {
                b0 = (byte)this.in.read();
                i |= (b0 & 127) << j++ * 7;
                if (j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while((b0 & 128) == 128);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return i;
    }

    public long readVarLong() {
        this.checkAvailable();
        long i = 0L;
        int j = 0;

        byte b0;
        try {
            do {
                b0 = (byte)this.in.read();
                i |= (long)((b0 & 127) << j++ * 7);
                if (j > 10) {
                    throw new RuntimeException("VarLong too big");
                }
            } while((b0 & 128) == 128);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return i;
    }

    public int readByte() {
        this.checkAvailable();
        return this.in.read();
    }

    public int readUnsignedByte() {
        this.checkAvailable();
        return this.in.read() & 255;
    }

    public float readFloat() {
        this.checkAvailable();
        return Float.intBitsToFloat(this.readInt());
    }

    public double readDouble() {
        this.checkAvailable();
        return Double.longBitsToDouble(this.readLong());
    }

    public static int readVarInt(DataInputStream dis) {
        int i = 0;
        int j = 0;

        byte b0;
        try {
            do {
                b0 = dis.readByte();
                i |= (b0 & 127) << j++ * 7;
                if (j > 5) {
                    throw new RuntimeException("VarInt too big");
                }
            } while((b0 & 128) == 128);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        return i;
    }

    public static long readVarLong(DataInputStream dis) {
        long i = 0L;
        int j = 0;

        byte b0;
        try {
            do {
                b0 = dis.readByte();
                i |= (long)((b0 & 127) << j++ * 7);
                if (j > 10) {
                    throw new RuntimeException("VarLong too big");
                }
            } while((b0 & 128) == 128);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return i;
    }

    private void checkAvailable() {
        if (this.in.available() <= 0) {
            throw new IllegalStateException("You are attempting to read a finished buffer.");
        }
    }
}
