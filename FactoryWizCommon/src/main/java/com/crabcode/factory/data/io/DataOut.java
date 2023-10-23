package com.crabcode.factory.data.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class DataOut {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream();

    public DataOut() {
    }

    public byte[] pop() {
        byte[] o = this.out.toByteArray();
        this.out.reset();
        return o;
    }

    public void writeBytes(byte[] value) {
        try {
            this.out.write(value);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void writeLong(long value) {
        try {
            this.out.write(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(value).array());
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void writeDouble(double value) {
        try {
            this.out.write(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putDouble(value).array());
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void writeFloat(float value) {
        try {
            this.out.write(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(value).array());
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void writeBoolean(boolean value) {
        this.out.write(value ? 1 : 0);
    }

    public void writeString(String value) {
        byte[] buf = value.getBytes(StandardCharsets.UTF_8);
        this.writeInt(buf.length);

        try {
            this.out.write(buf);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public void writeVarInt(int i) {
        while(true) {
            try {
                if ((i & -128) != 0) {
                    this.out.write(i & 127 | 128);
                    i >>>= 7;
                    continue;
                }

                this.out.write(i);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            return;
        }
    }

    public void writeVarLong(long i) {
        while(true) {
            try {
                if ((i & -128L) != 0L) {
                    this.out.write((int)(i & 127L) | 128);
                    i >>>= 7;
                    continue;
                }

                this.out.write((int)i);
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return;
        }
    }

    public void writeInt(int i) {
        try {
            this.out.write(new byte[]{(byte)(i >>> 24), (byte)(i >>> 16), (byte)(i >>> 8), (byte)i});
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void writeSignedShort(short i) {
        try {
            this.out.write(new byte[]{(byte)(i >> 8), (byte)i});
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void writeUnsignedShort(int i) {
        try {
            this.out.write(new byte[]{(byte)(i >>> 8 & 255), (byte)(i & 255)});
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    /** @deprecated */
    @Deprecated
    public void writeLittleShort(short i) {
        try {
            this.out.write(new byte[]{(byte)i, (byte)(i >> 8)});
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void writeByte(int id) {
        this.out.write(id);
    }

    public static void writeVarInt(DataOutputStream dos, int i) {
        while(true) {
            try {
                if ((i & -128) != 0) {
                    dos.writeByte(i & 127 | 128);
                    i >>>= 7;
                    continue;
                }

                dos.writeByte(i);
            } catch (Exception var3) {
                var3.printStackTrace();
            }

            return;
        }
    }

    public static void writeVarLong(DataOutputStream dos, long i) {
        while(true) {
            try {
                if ((i & -128L) != 0L) {
                    dos.writeByte((int)(i & 127L) | 128);
                    i >>>= 7;
                    continue;
                }

                dos.writeByte((int)i);
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return;
        }
    }
}

