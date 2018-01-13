package sgi.javaMacros.model.enums;

public enum UseCaseType {
	MANUAL, 
WINDOWS_CLASS, WINDOWS_TITLE;
//
//	static {
//		UseCaseType[] values = values();
//		for (UseCaseType next : values) {
//			next.getMatchModes();
//		}
//	}
//
//	private UseCaseMatchMode[] matchModes;
//
//	public boolean isMatchModeCompatible(UseCaseMatchMode mode) {
//		return mode.isUseCaseTypeCompatible(this);
//	}
//
//	public UseCaseMatchMode[] filterModes() {
//
//		ArrayList<UseCaseMatchMode> arrayList = new ArrayList<>(Arrays.asList(UseCaseMatchMode.values()));
//		Iterator<UseCaseMatchMode> iter = arrayList.iterator();
//		while (iter.hasNext()) {
//			UseCaseMatchMode uMode = (UseCaseMatchMode) iter.next();
//			if (!uMode.isUseCaseTypeCompatible(this))
//				iter.remove();
//
//		}
//
//		UseCaseMatchMode[] a = new UseCaseMatchMode[arrayList.size()];
//
//		arrayList.toArray(a);
//
//		return a;
//
//	};
//
//	public UseCaseMatchMode[] getMatchModes() {
//		if (matchModes == null)
//			matchModes = filterModes();
//		return matchModes;
//	}

}
