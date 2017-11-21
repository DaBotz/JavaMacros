package sgi.javaMacros.os.windows;

import static java.util.Arrays.asList;

import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface DeviceAccess extends StdCallLibrary {
	
	public static final UINT RIDI_DEVICEINFO = new UINT(0x2000000b);
	public static final int RIM_TYPEMOUSE = 0; //new DWORD(0x0);
	public static final int RIM_TYPE_KEYBOARD =1; // new DWORD(1);




	public static class RID_DEVICE_INFO_MOUSE extends Structure {
	  public DWORD dwId;
	  public DWORD dwNumberOfButtons;
	  public DWORD dwSampleRate;
	  public BOOL fHasHorizontalWheel;

	  @Override
	  protected List<String> getFieldOrder() {
	    return asList("dwId", "dwNumberOfButtons", "dwSampleRate", "fHasHorizontalWheel");
	  }
	}

	public static class RID_DEVICE_INFO_KEYBOARD extends Structure {
	  public DWORD dwType;
	  public DWORD dwSubType;
	  public DWORD dwKeyboardMode;
	  public DWORD dwNumberOfFunctionKeys;
	  public DWORD dwNumberOfIndicators;
	  public DWORD dwNumberOfKeysTotal;

	  @Override
	  protected List<String> getFieldOrder() {
	    return asList("dwType", "dwSubType", "dwKeyboardMode", "dwNumberOfFunctionKeys", "dwNumberOfIndicators", "dwNumberOfKeysTotal");
	  }
	}

	public static class RID_DEVICE_INFO_HID extends Structure {
	  public DWORD dwVendorId;
	  public DWORD dwProductId;
	  public DWORD dwVersionNumber;
	  public USHORT usUsagePage;
	  public USHORT usUsage;

	  @Override
	  protected List<String> getFieldOrder() {
	    return asList("dwVendorId", "dwProductId", "dwVersionNumber", "usUsagePage", "usUsage");
	  }
	}

	
    public static class RID_DEVICE_INFO extends Structure {
        public int cbSize;
        public int dwType;
        public RID_DEVICE_INFO_ ridDeviceInfo_;

        // Ensure the active field corresponds to what is read back from native memory
        public void read() {
            super.read();
            Class type = RID_DEVICE_INFO_HID.class;
            switch(dwType) {
            case RIM_TYPEMOUSE:
                type = RID_DEVICE_INFO_MOUSE.class; break;
            case RIM_TYPE_KEYBOARD:
                type = RID_DEVICE_INFO_KEYBOARD.class; break;
            default:
                break;
            }
            ridDeviceInfo_.setType(type);
            super.read(); 
        }

        @Override
        protected List<String> getFieldOrder() {
            return asList("cbSize", "dwType", "ridDeviceInfo_");
        }
    }

    public static class RID_DEVICE_INFO_ extends Union {
        public RID_DEVICE_INFO_MOUSE mouse;
        public RID_DEVICE_INFO_KEYBOARD keyboard;
        public RID_DEVICE_INFO_HID hid;
    }

    public UINT GetRawInputDeviceInfo(HANDLE hDevice, UINT uiCommand, RID_DEVICE_INFO pData, IntByReference pcbSize);
}