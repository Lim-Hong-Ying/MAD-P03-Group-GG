package sg.edu.np.mad_p03_group_gg.models;

public class AdBannerImage {
    // To populate the advertisement banners on the homepage, or MainActivity

    private String bitmapFilePath;

    public AdBannerImage(String bitmapFilePath) {
        this.bitmapFilePath = bitmapFilePath;
    }

    public String getImage() {
        return bitmapFilePath;
    }
}
