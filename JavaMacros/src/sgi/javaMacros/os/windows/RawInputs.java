package sgi.javaMacros.os.windows;

import static java.util.Arrays.asList;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.PVOID;
import com.sun.jna.platform.win32.WinDef.UINT;
import com.sun.jna.platform.win32.WinDef.ULONG;
import com.sun.jna.platform.win32.WinDef.USHORT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import sgi.javaMacros.os.windows.RawInputs.WindowsRawInput.RID_DEVICE_INFO;
import sgi.javaMacros.os.windows.RawInputs.WindowsRawInput.RID_DEVICE_INFO_HID;
import sgi.os.windows.HIDPages;
import sgi.os.windows.HIDPages.HIDPage;

public class RawInputs {

	public static RID_DEVICE_INFO readDevice(HANDLE hDevice) {

		UINT uiCommand = WindowsRawInput.RIDI_DEVICEINFO;
		RID_DEVICE_INFO deviceInfo = new RID_DEVICE_INFO();
		// Could also just put this in the constructor
		deviceInfo.cbSize = deviceInfo.size();

		IntByReference pcbSize = new IntByReference(deviceInfo.size());
		// UINT lResult =
		WindowsRawInput.INSTANCE.GetRawInputDeviceInfo(hDevice, uiCommand, deviceInfo, pcbSize);
		deviceInfo.read();
		return deviceInfo;

	}

	public static void main(String[] args) throws InterruptedException {
		String oDs, nDs = oDs = rawDevicesSnapshot();

		long ctm = System.currentTimeMillis() + 60000;
		while (System.currentTimeMillis() < ctm) {
			Thread.sleep(2000);
			if (!oDs.equals(nDs = rawDevicesSnapshot()))
				System.out.println("Something has changed?");
			oDs = nDs;

		}

	}

	public static String rawDevicesSnapshot() {
		StringBuffer buffer = new StringBuffer();

		IntByReference lNumDevices = new IntByReference();
		WindowsRawInput.RAWINPUTDEVICELIST lRawInputDevice = new WindowsRawInput.RAWINPUTDEVICELIST();
		int lRawInputSize = lRawInputDevice.size();

		// getting the size of devices to get and setting the structure size
		WindowsRawInput.INSTANCE.GetRawInputDeviceList(null, lNumDevices, lRawInputSize);

		// creating the device list
		WindowsRawInput.RAWINPUTDEVICELIST[] lDevices = (WindowsRawInput.RAWINPUTDEVICELIST[]) lRawInputDevice
				.toArray(lNumDevices.getValue());

		WindowsRawInput.INSTANCE.GetRawInputDeviceList(lDevices[0], lNumDevices, lRawInputSize);

		buffer.append("\n");
		buffer.append("devices deteced=" + lNumDevices.getValue());
		for (int i = 0; i < lDevices.length; i++) {

			DWORD dwType = lDevices[i].dwType;
			HANDLE hDevice = lDevices[i].hDevice;
			if (dwType.intValue() > 1) {
				buffer.append("\n");
				buffer.append(decode(hDevice) + "\ndevice type: " + dwType);
				RID_DEVICE_INFO info = readDevice(hDevice);
				RID_DEVICE_INFO_HID hid = info.ridDeviceInfo_.hid;
				Field[] declaredFields = hid.getClass().getDeclaredFields();
				for (int j = 0; j < declaredFields.length; j++) {
					Field field = declaredFields[j];
					field.setAccessible(true);

					try {
						buffer.append("\n");
						buffer.append("" + field.getName() + ":= " + decode(field.get(hid)));
					} catch (Throwable e) {
						e.printStackTrace();
					}

				}

				HIDPage usagePage = HIDPages.getPage(hid.usUsagePage.intValue());
				if (usagePage != null) {
					buffer.append("\n");
					buffer.append(usagePage.getPageDescription() + ": " + usagePage.get(hid.usUsage.intValue()));
				}
				buffer.append("\n");
				// buffer.append("Serial:" + HID.getSerialAsString(hDevice));
			}
		}

		return buffer.toString();

	}

	private static String decode(Object object) {

		String valueOf = String.valueOf(object);
		if (object instanceof Integer)
			valueOf += (" [" + Integer.toHexString(((Integer) object).intValue()) + "]");
		if (object instanceof DWORD) {
			valueOf += (" [" + Integer.toHexString(((DWORD) object).intValue()) + "]");
		}
		if (object instanceof HANDLE) {
			// HANDLE h = (HANDLE) object;
			// HANDLE native@0x2001a (com.sun.jna.platform.win32.WinNT$HANDLE@2001a)

			// return "HANDLE"
			//
			int lastAt = valueOf.lastIndexOf('@');
			String hexVal = valueOf.substring(lastAt + 1);
			lastAt = hexVal.lastIndexOf(')');
			hexVal = hexVal.substring(0, lastAt);
			// buffer.append("\n"); buffer.append(hexVal);
			int v = Integer.parseInt(hexVal, 16);
			return "HANDLE " + v + " [" + hexVal + "]";
		}

		return valueOf;
	}

	public interface HID extends StdCallLibrary {

		HID INSTANCE = (HID) Native.loadLibrary("hid", HID.class, W32APIOptions.DEFAULT_OPTIONS);

		public boolean HidD_GetManufacturerString(HANDLE HidDeviceObject, PVOID Buffer, ULONG BufferLength);

		public boolean HidD_GetSerialNumberString(HANDLE HidDeviceObject, byte[] Buffer, ULONG BufferLength);

		public boolean HidD_GetPhysicalDescriptor(HANDLE HidDeviceObject, byte[] Buffer, ULONG BufferLength);

		public boolean HidD_GetIndexedString( /* _In_ */ HANDLE HidDeviceObject, /* _In_ */ ULONG StringIndex,
				/* _Out_ */ byte[] buffer, /* _In_ */ ULONG BufferLength);

		public boolean HidD_GetProductString(/* _In_ */ HANDLE HidDeviceObject, /* _Out_ */ byte[] Buffer,
				/* _In_ */ ULONG BufferLength);

		/*
		 * HidD_FlushQueue HidD_FreePreparsedData HidD_GetAttributes
		 * HidD_GetConfiguration HidD_GetFeature HidD_GetHidGuid HidD_GetIndexedString
		 * HidD_GetInputReport HidD_GetManufacturerString HidD_GetMsGenreDescriptor
		 * HidD_GetNumInputBuffers HidD_GetPhysicalDescriptor HidD_GetPreparsedData
		 * HidD_GetProductString HidD_GetSerialNumberString HidD_Hello
		 * HidD_SetConfiguration HidD_SetFeature HidD_SetNumInputBuffers
		 * HidD_SetOutputReport HidP_GetButtonCaps HidP_GetCaps HidP_GetData
		 * HidP_GetExtendedAttributes HidP_GetLinkCollectionNodes
		 * HidP_GetScaledUsageValue HidP_GetSpecificButtonCaps HidP_GetSpecificValueCaps
		 * HidP_GetUsageValue HidP_GetUsageValueArray HidP_GetUsages HidP_GetUsagesEx
		 * HidP_GetValueCaps HidP_InitializeReportForID HidP_MaxDataListLength
		 * HidP_MaxUsageListLength HidP_SetData HidP_SetScaledUsageValue
		 * HidP_SetUsageValue HidP_SetUsageValueArray HidP_SetUsages
		 * HidP_TranslateUsagesToI8042ScanCodes HidP_UnsetUsages
		 * HidP_UsageListDifference
		 */

		// public static String getSerialAsString(HANDLE hDevice) {
		//
		// int length = 128 * 32;
		// byte[] buffer = new byte[length];
		// if (!HID.INSTANCE.HidD_GetSerialNumberString(hDevice, buffer, new
		// ULONG(length))) {
		// System.err.println("HidD_GetSerialNumberString failed");
		// }
		// return Native.toString(buffer);
		//
		// }

	}

	public interface WindowsRawInput extends StdCallLibrary {

		WindowsRawInput INSTANCE = (WindowsRawInput) Native.loadLibrary("user32", WindowsRawInput.class,
				W32APIOptions.DEFAULT_OPTIONS);

		UINT GetRawInputDeviceList(RAWINPUTDEVICELIST pRawInputDeviceList, IntByReference puiNumDevices, int cbSize);

		public static class RAWINPUTDEVICELIST extends Structure {

			public HANDLE hDevice;
			public DWORD dwType;

			public RAWINPUTDEVICELIST() {
			}

			@Override
			protected List<String> getFieldOrder() {
				return Arrays.asList(new String[] { "hDevice", "dwType" });
			}

		}

		public static final UINT RIDI_DEVICEINFO = new UINT(0x2000000b);
		public static final int RIM_TYPEMOUSE = 0; // new DWORD(0x0);
		public static final int RIM_TYPE_KEYBOARD = 1; // new DWORD(1);

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
				return asList("dwType", "dwSubType", "dwKeyboardMode", "dwNumberOfFunctionKeys", "dwNumberOfIndicators",
						"dwNumberOfKeysTotal");
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

			// Ensure the active field corresponds to what is read back from
			// native memory
			public void read() {
				super.read();
				@SuppressWarnings("rawtypes")
				Class type = RID_DEVICE_INFO_HID.class;
				switch (dwType) {
				case RIM_TYPEMOUSE:
					type = RID_DEVICE_INFO_MOUSE.class;
					break;
				case RIM_TYPE_KEYBOARD:
					type = RID_DEVICE_INFO_KEYBOARD.class;
					break;
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

		public UINT GetRawInputDeviceInfo(HANDLE hDevice, UINT uiCommand, RID_DEVICE_INFO pData,
				IntByReference pcbSize);

	}

}