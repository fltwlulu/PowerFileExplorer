package com.amaze.filemanager.utils;

import com.amaze.filemanager.ui.drawer.Item;
import com.cloudrail.si.interfaces.CloudStorage;
import com.cloudrail.si.services.Box;
import com.cloudrail.si.services.Dropbox;
import com.cloudrail.si.services.GoogleDrive;
import com.cloudrail.si.services.OneDrive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by arpitkh996 on 20-01-2016.
 *
 * Singleton class to handle data for various services
 */

//Central data being used across activity,fragments and classes
public class DataUtils {

    public static final int DELETE = 0, COPY = 1, MOVE = 2, NEW_FOLDER = 3,
	RENAME = 4, NEW_FILE = 5, EXTRACT = 6, COMPRESS = 7, UPDATE_ZIP = 8, DELETE_IN_ZIP = 9, ADD_TO_ZIP = 10, EXTRACT_COPY_FROM_ZIP = 11, EXTRACT_MOVE_FROM_ZIP = 12;

    private ArrayList<String> hiddenfiles = new ArrayList<>(), gridfiles = new ArrayList<>(),
	listfiles = new ArrayList<>(), history = new ArrayList<>(), storages = new ArrayList<>();

    private ArrayList<Item> list = new ArrayList<>();
    private ArrayList<String[]> servers = new ArrayList<>(), books = new ArrayList<>();

    private ArrayList<CloudStorage> accounts = new ArrayList<>(4);

    private DataChangeListener dataChangeListener;

    private static DataUtils sDataUtils;

    public static DataUtils getInstance() {
        if (sDataUtils == null) {
            sDataUtils = new DataUtils();
        }
        return sDataUtils;
    }

    public int containsServer(String[] a) {
        return contains(a, servers);
    }

    public int containsServer(String path) {

        synchronized (servers) {
            if (servers == null) 
				return -1;
            int i = 0;
            for (String[] x : servers) {
                if (x[1].equals(path)) return i;
                i++;
            }
        }
        return -1;
    }

    public int containsBooks(String[] a) {
        return contains(a, books);
    }

    /*public int containsAccounts(CloudEntry cloudEntry) {
	 return contains(a, accounts);
	 }*/

    /**
     * Checks whether cloud account of certain type is present or not
     * @param serviceType the {@link OpenMode} of account to check
     * @return the index of account, -1 if not found
     */
    public synchronized int containsAccounts(OpenMode serviceType) {
        int i = 0;
        for (CloudStorage storage : accounts) {

            switch (serviceType) {
                case BOX:
                    if (storage instanceof Box)
                        return i;
                    break;
                case DROPBOX:
                    if (storage instanceof Dropbox)
                        return i;
                    break;
                case GDRIVE:
                    if (storage instanceof GoogleDrive)
                        return i;
                    break;
                case ONEDRIVE:
                    if (storage instanceof OneDrive)
                        return i;
                    break;
                default:
                    return -1;
            }
            i++;
        }
        return -1;
    }

    public void clear() {
        hiddenfiles = new ArrayList<>();
        gridfiles = new ArrayList<>();
        listfiles = new ArrayList<>();
        history = new ArrayList<>();
        storages = new ArrayList<>();
        servers = new ArrayList<>();
        books = new ArrayList<>();
        accounts = new ArrayList<>();
    }

    public void registerOnDataChangedListener(DataChangeListener l) {
        dataChangeListener = l;
        clear();
    }

//    int contains(String a, ArrayList<String[]> b) {
//        int i = 0;
//        for (String[] x : b) {
//            if (x[1].equals(a)) return i;
//            i++;
//
//        }
//        return -1;
//    }

    int contains(String[] a, ArrayList<String[]> b) {
        if (b == null) return -1;
        int i = 0;
        for (String[] x : b) {
            if (x[0].equals(a[0]) && x[1].equals(a[1])) return i;
            i++;

        }
        return -1;
    }

    public synchronized void removeAccount(OpenMode serviceType) {
        for (CloudStorage storage : accounts) {
            switch (serviceType) {
                case BOX:
                    if (storage instanceof Box) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                case DROPBOX:
                    if (storage instanceof Dropbox) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                case GDRIVE:
                    if (storage instanceof GoogleDrive) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                case ONEDRIVE:
                    if (storage instanceof OneDrive) {
                        accounts.remove(storage);
                        return;
                    }
                    break;
                default:
                    return;
            }
        }
    }

    public void removeServer(int i) {
        synchronized (servers) {
            if (servers.size() > i)
                servers.remove(i);
        }
    }

    public void removeBook(int i) {
        synchronized (books) {
            if (books.size() > i)
                books.remove(i);
        }
    }

    public void addBook(String[] strs) {
        synchronized (books) {
            if (containsBooks(strs) < 0) {
				books.add(strs);
			}
        }
    }

    public void addBook(final String[] strs, boolean refreshdrawer) {
        synchronized (books) {
			if (containsBooks(strs) < 0) {
				books.add(strs);
				if (refreshdrawer && dataChangeListener != null) {
					AppConfig.runInBackground(new Runnable() {
							@Override
							public void run() {
								dataChangeListener.onBookAdded(strs, true);
							}
						});
				}
			}
        }
    }

    public void addAccount(CloudStorage storage) {
        accounts.add(storage);
    }

    public void addServer(String[] i) {
        servers.add(i);
    }

    public void addHiddenFile(final String str) {
        synchronized (hiddenfiles) {
            hiddenfiles.remove(str);
			hiddenfiles.add(0, str);
			if (dataChangeListener != null) {
				AppConfig.runInBackground(new Runnable() {
						@Override
						public void run() {
							dataChangeListener.onHiddenFileAdded(str);
						}
					});
			}
        }
    }

    public void removeHiddenFile(final String str) {
        synchronized (hiddenfiles) {
            hiddenfiles.remove(str);
			if (dataChangeListener != null) {
				AppConfig.runInBackground(new Runnable() {
						@Override
						public void run() {
							dataChangeListener.onHiddenFileRemoved(str);
						}
					});
			}
        }
    }

    public ArrayList<String> getHistory() {
        return history;
    }

    public void clearHistory() {
        history.clear();// = new ArrayList<>();
        if (dataChangeListener != null) {
            AppConfig.runInBackground(new Runnable() {
					@Override
					public void run() {
						dataChangeListener.onHistoryCleared();
					}
				});
        }
    }

    public void addHistoryFile(final String historyFile) {

        synchronized (history) {
			history.remove(historyFile);
            history.add(0, historyFile);
			if (dataChangeListener != null) {
				AppConfig.runInBackground(new Runnable() {
						@Override
						public void run() {
							dataChangeListener.onHistoryAdded(historyFile);
						}
					});
			}
        }
    }

    public void sortBook() {
        Collections.sort(books, new BookSorter());
    }

    public synchronized void setServers(ArrayList<String[]> servers) {
        if (servers != null)
            this.servers = servers;
    }

    public synchronized void setBooks(ArrayList<String[]> books) {
        if (books != null)
            this.books = books;
    }

    public synchronized void setAccounts(ArrayList<CloudStorage> accounts) {
        if (accounts != null)
            this.accounts = accounts;
    }

    public synchronized ArrayList<String[]> getServers() {
        return servers;
    }

    public synchronized ArrayList<String[]> getBooks() {
        return books;
    }

    public synchronized ArrayList<CloudStorage> getAccounts() {
        return accounts;
    }

    public synchronized CloudStorage getAccount(OpenMode serviceType) {
        for (CloudStorage storage : accounts) {
            switch (serviceType) {
                case BOX:
                    if (storage instanceof Box)
                        return storage;
                    break;
                case DROPBOX:
                    if (storage instanceof Dropbox)
                        return storage;
                    break;
                case GDRIVE:
                    if (storage instanceof GoogleDrive)
                        return storage;
                    break;
                case ONEDRIVE:
                    if (storage instanceof OneDrive)
                        return storage;
                    break;
                default:
                    return null;
            }
        }
        return null;
    }

    public ArrayList<String> getHiddenfiles() {
        return hiddenfiles;
    }

    public synchronized void setHiddenfiles(ArrayList<String> hiddenfiles) {
        if (hiddenfiles != null)
            this.hiddenfiles = hiddenfiles;
    }

    public ArrayList<String> getGridFiles() {
        return gridfiles;
    }

    public synchronized void setGridfiles(ArrayList<String> gridfiles) {
        if (gridfiles != null)
            this.gridfiles = gridfiles;
    }

    public ArrayList<String> getListfiles() {
        return listfiles;
    }

    public synchronized void setListfiles(ArrayList<String> listfiles) {
        if (listfiles != null)
            this.listfiles = listfiles;
    }

    public synchronized List<String> getStorages() {
        return storages;
    }

    public synchronized void setStorages(ArrayList<String> storages) {
        this.storages = storages;
    }

    public ArrayList<Item> getList() {
        return list;
    }

    public synchronized void setList(ArrayList<Item> list) {
        this.list = list;
    }

    /**
     * Callbacks to do original changes in database (and ui if required)
     * The callbacks are called in a background thread
     */
    public interface DataChangeListener {
        void onHiddenFileAdded(String path);

        void onHiddenFileRemoved(String path);

        void onHistoryAdded(String path);

        void onBookAdded(String path[], boolean refreshdrawer);

        void onHistoryCleared();
    }

}
