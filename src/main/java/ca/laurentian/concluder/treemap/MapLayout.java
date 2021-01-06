package ca.laurentian.concluder.treemap;

public interface MapLayout {
    /**
     * Arrange the items in the given MapModel to fill the given rectangle.
     *
     * @param model The MapModel.
     * @param bounds The boundsing rectangle for the layout.
     */
    public void layout(MapModel model, Rect bounds);

    /**
     * Return a human-readable name for this layout;
     * used to label figures, tables, etc.
     *
     * @return String naming this layout.
     */
    public String getName();

    /**
     * Return a longer description of this layout;
     * Helpful in creating online-help,
     * interactive catalogs or indices to lists of algorithms.
     *
     * @return String describing this layout.
     */
    public String getDescription();
}
