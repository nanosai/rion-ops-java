package com.nanosai.rionops.rion.read.object;

import java.io.UnsupportedEncodingException;

/**
 * A key for a key field. The KeyFieldKey is used as key to find a
 *
 * Created by jjenkov on 05-11-2015.
 */
public class RionKeyFieldKey {
    byte[] source = null;
    int startOffset = 0;  //the start offset of the *value* of the key field (after lead byte + length bytes) - not the start offset of the key field itself (lead byte).
    int    length   = 0;

    int    hashCode = -1;

    public RionKeyFieldKey() {
    }

    public RionKeyFieldKey(byte[] source){
        this(source, 0, source.length);
    }

    public RionKeyFieldKey(byte[] source, int startOffset, int length) {
        this.source = source;

        setOffsets(startOffset, length);
    }

    public void setSource(byte[] source){
        this.source = source;
    }

    public void setOffsets(int startOffset, int length){
        this.startOffset = startOffset;
        this.length = length;

        this.calcHashCode();
    }


    @Override
    public int hashCode() {
        return this.hashCode;
    }


    int calcHashCode() {
        this.hashCode = 0;
        for(int i= startOffset, n= startOffset + length; i<n; i++){
            this.hashCode += source[i];
        }
        return this.hashCode;
    }


    @Override
    public boolean equals(Object obj) {
        RionKeyFieldKey otherKey = (RionKeyFieldKey) obj;

        if(this.hashCode != otherKey.hashCode){ return false; }
        if(this.length   != otherKey.length)  { return false; }

        for(int i=0; i<length; i++){
            if(this.source[this.startOffset + i] != otherKey.source[otherKey.startOffset + i]){
                return false;
            }
        }
        return true;
    }

    public int length() {
        return this.length;
    }

    @Override
    public String toString() {
        try {
            return new String(source, startOffset, length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); //won't happen - UTF-8 always supported.
            return null;
        }
    }
}
