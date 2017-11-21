package sgi.javaMacros.os.windows;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public  class RawInputDeviceList extends Structure {

    public HANDLE hDevice;
    public DWORD dwType;

    public RawInputDeviceList() {
            // required for toArray()
    }

    public RawInputDeviceList(Pointer pointer) {
        super(pointer);
        read();
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(new String[] { "hDevice", "dwType" });
    }

}