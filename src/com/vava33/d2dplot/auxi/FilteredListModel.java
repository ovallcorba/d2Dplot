package com.vava33.d2dplot.auxi;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class FilteredListModel extends AbstractListModel<Object> {

    private static final long serialVersionUID = 4491767425231601431L;

    public static interface Filter {
        boolean accept(Object element);
    }

    private final ListModel<?> _source;
    private Filter _filter;
    private final ArrayList<Integer> _indices = new ArrayList<Integer>();

    public FilteredListModel(ListModel<?> source) {
        if (source == null)
            throw new IllegalArgumentException("Source is null");
        this._source = source;
        this._source.addListDataListener(new ListDataListener() {
            @Override
            public void intervalRemoved(ListDataEvent e) {
                FilteredListModel.this.doFilter();
            }

            @Override
            public void intervalAdded(ListDataEvent e) {
                FilteredListModel.this.doFilter();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                FilteredListModel.this.doFilter();
            }
        });
    }

    public void setFilter(Filter f) {
        this._filter = f;
        this.doFilter();
    }

    private void doFilter() {
        this._indices.clear();

        final Filter f = this._filter;
        if (f != null) {
            final int count = this._source.getSize();
            for (int i = 0; i < count; i++) {
                final Object element = this._source.getElementAt(i);
                if (f.accept(element)) {
                    this._indices.add(i);
                }
            }
            this.fireContentsChanged(this, 0, this.getSize() - 1);
        }
    }

    @Override
    public int getSize() {
        return (this._filter != null) ? this._indices.size() : this._source.getSize();
    }

    @Override
    public Object getElementAt(int index) {
        return (this._filter != null) ? this._source.getElementAt(this._indices.get(index))
                : this._source.getElementAt(index);
    }
}