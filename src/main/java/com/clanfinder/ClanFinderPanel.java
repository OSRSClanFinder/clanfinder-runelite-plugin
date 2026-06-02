package com.clanfinder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import net.runelite.client.plugins.worldhopper.WorldHopperPlugin;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;

final class ClanFinderPanel extends PluginPanel
{
    private static final Color ICON_GOLD = new Color(207, 151, 45);
    private static final Color ICON_GREEN = new Color(72, 151, 91);
    private static final Color ICON_RED = new Color(184, 79, 72);
    private static final Color ICON_BLUE = new Color(73, 128, 180);
    private static final Color ICON_PURPLE = new Color(132, 104, 179);
    private static final Color ICON_NEUTRAL = new Color(108, 116, 128);
    private static final Color PANEL_BACKGROUND = new Color(28, 28, 28);
    private static final Color CARD_BACKGROUND = new Color(39, 40, 42);
    private static final Color CARD_BORDER = new Color(62, 64, 68);
    private static final Color FEATURED_CARD_BORDER = new Color(185, 119, 35);
    private static final Color DESCRIPTION_BACKGROUND = new Color(34, 35, 38);
    private static final Color DESCRIPTION_BORDER = new Color(54, 57, 63);
    private static final Color FIELD_BACKGROUND = new Color(48, 49, 52);
    private static final Color TEXT_PRIMARY = new Color(236, 236, 236);
    private static final Color TEXT_SECONDARY = new Color(176, 180, 186);
    private static final Color TEXT_MUTED = new Color(132, 138, 146);
    private static final Color CHIP_BACKGROUND = new Color(52, 54, 58);
    private static final Color CHIP_BORDER = new Color(74, 77, 84);
    private static final Color ACTION_BACKGROUND = new Color(62, 65, 71);
    private static final Color ACTION_PRIMARY = new Color(70, 94, 122);
    private static final int CARD_WIDTH = PluginPanel.PANEL_WIDTH - 36;
    private static final int CONTENT_TEXT_WIDTH = CARD_WIDTH - 28;
    private static final int CONTENT_ACTION_WIDTH = CONTENT_TEXT_WIDTH;
    private static final int SEE_MORE_BUTTON_WIDTH = 154;
    private static final int BANNER_WIDTH = CONTENT_TEXT_WIDTH;
    private static final int BANNER_HEIGHT = 76;
    private static final int TYPE_BADGE_WIDTH = CONTENT_TEXT_WIDTH;
    private static final int TYPE_BADGE_HEIGHT = 30;
    private static final int TYPE_BADGE_ICON_SIZE = 16;
    private static final float CLAN_NAME_FONT_SIZE = 15.6f;
    private static final float MEMBER_FONT_SIZE = 11.8f;
    private static final float SECTION_LABEL_FONT_SIZE = 12.2f;
    private static final float BODY_TEXT_FONT_SIZE = 13.6f;
    private static final float DESCRIPTION_FONT_SIZE = BODY_TEXT_FONT_SIZE;
    private static final float DETAIL_DESCRIPTION_FONT_SIZE = BODY_TEXT_FONT_SIZE;
    private static final float REQUIREMENTS_LABEL_FONT_SIZE = 11.7f;
    private static final float REQUIREMENTS_FONT_SIZE = BODY_TEXT_FONT_SIZE;
    private static final float EVENT_TEXT_FONT_SIZE = BODY_TEXT_FONT_SIZE;
    private static final float TYPE_BADGE_FONT_SIZE = 11.8f;
    private static final float BACK_BUTTON_FONT_SIZE = 13.4f;
    private static final int SANTA_HAT_WIDTH = 17;
    private static final int SANTA_HAT_HEIGHT = 14;
    private static final DateTimeFormatter EVENT_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, h:mm a", Locale.US);

    private static final ClanTypeOption[] TYPES = {
        new ClanTypeOption("All types", "", new AllTypesIcon()),
        new ClanTypeOption("Social", "Social", typeIcon("Social", 18)),
        new ClanTypeOption("PvM", "PvM", typeIcon("PvM", 18)),
        new ClanTypeOption("Raids", "Raids", typeIcon("Raids", 18)),
        new ClanTypeOption("Skilling", "Skilling", typeIcon("Skilling", 18)),
        new ClanTypeOption("Merchants", "Merchants", typeIcon("Merchants", 18)),
        new ClanTypeOption("Ironman", "Ironman", typeIcon("Ironman", 18)),
        new ClanTypeOption("Hardcore Ironman", "Hardcore Ironman", typeIcon("Hardcore Ironman", 18)),
        new ClanTypeOption("PvP", "PvP", typeIcon("PvP", 18)),
        new ClanTypeOption("Minigames", "Minigames", typeIcon("Minigames", 18)),
        new ClanTypeOption("Bossing", "Bossing", typeIcon("Bossing", 18)),
        new ClanTypeOption("Collection Log", "Collection Log", typeIcon("Collection Log", 18)),
        new ClanTypeOption("New Player", "New Player", typeIcon("New Player", 18)),
        new ClanTypeOption("Completionist", "Completionist", typeIcon("Completionist", 18)),
        new ClanTypeOption("F2P", "F2P", typeIcon("F2P", 18))
    };

    private static final RegionOption[] REGIONS = {
        new RegionOption("All regions", "", new GlobeRegionIcon()),
        new RegionOption("Global", "Global", new GlobeRegionIcon()),
        new RegionOption("United States", "United States", worldFlagIcon("flag_us.png")),
        new RegionOption("United Kingdom", "United Kingdom", worldFlagIcon("flag_uk.png")),
        new RegionOption("Germany", "Germany", worldFlagIcon("flag_ger.png")),
        new RegionOption("Australia", "Australia", worldFlagIcon("flag_aus.png")),
        new RegionOption("Brazil", "Brazil", worldFlagIcon("flag_br.png")),
        new RegionOption("Other", "Other", new GlobeRegionIcon())
    };

    private final ClanFinderPanelListener listener;
    private final JTextField searchField = new PlaceholderTextField("Search Clans");
    private final JComboBox<ClanTypeOption> typeSelect = new JComboBox<>(TYPES);
    private final JComboBox<RegionOption> regionSelect = new JComboBox<>(REGIONS);
    private final JLabel statusLabel = new JLabel("Ready");
    private final JPanel resultsPanel = new JPanel();
    private final List<ClanListing> displayedClans = new ArrayList<>();
    private ClanSearchQuery currentQuery = new ClanSearchQuery("", "", "", 25);
    private int currentPage = 1;
    private int displayedCount;
    private int totalCount;
    private boolean loadingMore;

    ClanFinderPanel(ClanFinderPanelListener listener)
    {
        super(false);
        this.listener = listener;

        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(PANEL_BACKGROUND);
        setOpaque(true);

        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(PANEL_BACKGROUND);
        resultsPanel.setOpaque(true);

        add(buildControlsWrapper(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(PANEL_BACKGROUND);
        scrollPane.setOpaque(true);
        scrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 10.5f));
        statusLabel.setBorder(new EmptyBorder(2, 2, 0, 2));
        add(statusLabel, BorderLayout.SOUTH);
    }

    void showLoading()
    {
        loadingMore = false;
        currentPage = 1;
        displayedCount = 0;
        totalCount = 0;
        displayedClans.clear();
        statusLabel.setText("Loading approved clans...");
        resultsPanel.removeAll();
        resultsPanel.add(buildStateCard(
            "Loading clans",
            "Fetching approved listings from ClanFinder."
        ));
        refreshResults();
    }

    void showLoadingMore()
    {
        loadingMore = true;
        removeSeeMoreButton();
        statusLabel.setText("Loading more approved clans...");
        JPanel loadingCard = buildStateCard(
            "Loading more clans",
            "Fetching the next page of approved listings."
        );
        loadingCard.setName("loading-more-clans");
        resultsPanel.add(loadingCard);
        refreshResults();
    }

    void showResults(ClanSearchResponse response)
    {
        List<ClanListing> clans = response.getData();
        ClanSearchPagination pagination = response.getPagination();
        boolean append = pagination.getPage() > 1;
        loadingMore = false;

        removeLoadingMoreMessage();

        if (!append)
        {
            resultsPanel.removeAll();
            displayedClans.clear();
            displayedCount = 0;
        }

        if (clans.isEmpty())
        {
            if (!append)
            {
                resultsPanel.add(buildStateCard(
                    "No clans found",
                    "No approved clans match these filters. Try a broader search or another region."
                ));
            }
        }
        else
        {
            displayedClans.addAll(clans);
            for (ClanListing clan : clans)
            {
                resultsPanel.add(buildClanCard(clan));
                resultsPanel.add(Box.createVerticalStrut(8));
            }
        }

        currentPage = Math.max(1, pagination.getPage());
        displayedCount += clans.size();
        totalCount = pagination.getTotal();
        addSeeMoreButtonIfNeeded();
        statusLabel.setText("Showing " + displayedCount + " of " + totalCount + " approved clans");
        refreshResults();
    }

    void showClanDetailLoading(ClanListing clan)
    {
        loadingMore = false;
        statusLabel.setText("Loading " + clan.getName() + " events...");
        resultsPanel.removeAll();
        resultsPanel.add(buildBackButtonRow());
        resultsPanel.add(Box.createVerticalStrut(8));
        resultsPanel.add(buildStateCard(
            "Loading clan details",
            "Fetching the latest profile and event details from ClanFinder."
        ));
        refreshResults();
    }

    void showClanDetail(ClanListing clan)
    {
        showClanDetail(clan, "");
    }

    void showClanDetail(ClanListing clan, String notice)
    {
        loadingMore = false;
        statusLabel.setText(clan.getName() + " events");
        resultsPanel.removeAll();
        resultsPanel.add(buildBackButtonRow());
        resultsPanel.add(Box.createVerticalStrut(8));
        resultsPanel.add(buildClanDetailPage(clan, notice));
        refreshResults();
    }

    void showLoadMoreError()
    {
        loadingMore = false;
        removeLoadingMoreMessage();
        addSeeMoreButtonIfNeeded();
        statusLabel.setText("Could not load more clans");
        refreshResults();
    }

    void showError(String message)
    {
        statusLabel.setText("Unable to load ClanFinder");
        resultsPanel.removeAll();
        resultsPanel.add(buildStateCard("Unable to load clans", message));
        refreshResults();
    }

    private JPanel buildControls()
    {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(PANEL_BACKGROUND);
        controls.setOpaque(true);
        typeSelect.setRenderer(new ClanTypeOptionRenderer());
        regionSelect.setRenderer(new RegionOptionRenderer());
        styleField(searchField);
        styleCombo(typeSelect);
        styleCombo(regionSelect);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 6, 0);

        controls.add(searchField, constraints);

        constraints.gridy++;
        controls.add(typeSelect, constraints);

        constraints.gridy++;
        controls.add(regionSelect, constraints);

        JButton refreshButton = new JButton("Refresh", new ReloadIcon());
        styleActionButton(refreshButton, ACTION_PRIMARY);
        refreshButton.setIconTextGap(7);
        refreshButton.addActionListener(event -> requestSearch());

        constraints.gridy++;
        controls.add(refreshButton, constraints);

        searchField.addActionListener(event -> requestSearch());
        typeSelect.addActionListener(event -> requestSearch());
        regionSelect.addActionListener(event -> requestSearch());

        Dimension preferred = controls.getPreferredSize();
        Dimension fixed = new Dimension(CARD_WIDTH, preferred.height);
        controls.setPreferredSize(fixed);
        controls.setMinimumSize(fixed);
        controls.setMaximumSize(fixed);
        return controls;
    }

    private JPanel buildControlsWrapper()
    {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setBackground(PANEL_BACKGROUND);
        wrapper.setOpaque(true);
        wrapper.add(buildControls());
        return wrapper;
    }

    private JPanel buildClanCard(ClanListing clan)
    {
        JPanel card = new RoundedPanel(CARD_BACKGROUND, clan.isSponsored() ? FEATURED_CARD_BORDER : CARD_BORDER, 8);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(CARD_WIDTH, Integer.MAX_VALUE));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel titleStack = new JPanel();
        titleStack.setOpaque(false);
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleStack.add(buildTitleRow(clan, true));
        titleStack.add(Box.createVerticalStrut(3));
        titleStack.add(buildMemberRow(clan));

        BannerImagePanel banner = buildBannerPanel(clan);
        if (banner != null)
        {
            makeClanDetailLink(banner, clan);
            titleStack.add(Box.createVerticalStrut(8));
            titleStack.add(banner);
        }

        String about = aboutSentenceText(clan);
        if (!about.isEmpty())
        {
            titleStack.add(Box.createVerticalStrut(8));
            titleStack.add(buildAboutSentencePanel(about, BANNER_WIDTH));
        }

        titleStack.add(Box.createVerticalStrut(9));
        titleStack.add(buildMetaRow(clan));

        if (!clan.getTypes().isEmpty())
        {
            titleStack.add(Box.createVerticalStrut(7));
            titleStack.add(buildTypeBadgeGrid(clan));
        }

        if (hasRequirements(clan))
        {
            titleStack.add(Box.createVerticalStrut(7));
            titleStack.add(buildRequirementsPanel(clan, CONTENT_TEXT_WIDTH));
        }

        if (hasEvents(clan))
        {
            titleStack.add(Box.createVerticalStrut(7));
            titleStack.add(buildEventCountLabel(clan, CONTENT_TEXT_WIDTH));
        }

        titleStack.add(Box.createVerticalStrut(10));

        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton copyButton = new JButton("Copy CC");
        styleActionButton(copyButton, ACTION_BACKGROUND);
        copyButton.setMaximumSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        copyButton.setPreferredSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        copyButton.setEnabled(!clan.getClanChatName().isEmpty());
        copyButton.addActionListener(event -> listener.copyClanChat(clan.getClanChatName()));

        JButton openButton = new JButton("Open");
        styleActionButton(openButton, ACTION_PRIMARY);
        openButton.setMaximumSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        openButton.setPreferredSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        openButton.addActionListener(event -> listener.openClan(clan.getSlug()));

        copyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        openButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.add(copyButton);
        actions.add(Box.createVerticalStrut(6));
        actions.add(openButton);

        titleStack.add(actions);
        card.add(titleStack);
        lockPanelWidth(card, CARD_WIDTH);
        return card;
    }

    private JPanel buildClanDetailPage(ClanListing clan, String notice)
    {
        JPanel page = new RoundedPanel(CARD_BACKGROUND, clan.isSponsored() ? FEATURED_CARD_BORDER : CARD_BORDER, 8);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setAlignmentX(Component.CENTER_ALIGNMENT);
        page.setMaximumSize(new Dimension(CARD_WIDTH, Integer.MAX_VALUE));
        page.setBorder(new EmptyBorder(10, 10, 10, 10));

        page.add(buildTitleRow(clan, false));
        page.add(Box.createVerticalStrut(3));
        page.add(buildMemberRow(clan));

        BannerImagePanel banner = buildBannerPanel(clan);
        if (banner != null)
        {
            page.add(Box.createVerticalStrut(8));
            page.add(banner);
        }

        if (!notice.isEmpty())
        {
            page.add(Box.createVerticalStrut(8));
            page.add(buildTextBlock(notice, CONTENT_TEXT_WIDTH, Font.PLAIN, TEXT_MUTED));
        }

        String description = detailDescriptionText(clan);
        if (!description.isEmpty())
        {
            page.add(Box.createVerticalStrut(10));
            page.add(sectionLabel("Description"));
            page.add(Box.createVerticalStrut(5));
            page.add(buildDetailDescriptionPanel(description, CONTENT_TEXT_WIDTH));
        }

        page.add(Box.createVerticalStrut(9));
        page.add(buildMetaRow(clan));

        if (!clan.getTypes().isEmpty())
        {
            page.add(Box.createVerticalStrut(9));
            page.add(sectionLabel("Categories"));
            page.add(Box.createVerticalStrut(5));
            page.add(buildTypeBadgeGrid(clan));
        }

        page.add(Box.createVerticalStrut(9));
        page.add(sectionLabel("Requirements"));
        page.add(Box.createVerticalStrut(4));
        page.add(buildDetailRequirementsPanel(clan, CONTENT_TEXT_WIDTH));

        List<ClanEvent> events = clan.getEvents();
        if (hasEvents(clan))
        {
            page.add(Box.createVerticalStrut(9));
            page.add(buildEventCountLabel(clan, CONTENT_TEXT_WIDTH));

            page.add(Box.createVerticalStrut(12));
            page.add(sectionLabel("Upcoming events"));
            page.add(Box.createVerticalStrut(6));

            for (ClanEvent event : events)
            {
                page.add(buildEventCard(event));
                page.add(Box.createVerticalStrut(7));
            }
        }

        page.add(Box.createVerticalStrut(5));
        page.add(buildDetailActions(clan));

        lockPanelWidth(page, CARD_WIDTH);
        return page;
    }

    private static void lockPanelWidth(JPanel panel, int width)
    {
        Dimension preferred = panel.getPreferredSize();
        Dimension fixed = new Dimension(width, preferred.height);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(new Dimension(width, Math.max(preferred.height, Short.MAX_VALUE)));
    }

    private BannerImagePanel buildBannerPanel(ClanListing clan)
    {
        String assetUrl = bannerAssetUrl(clan);
        if (assetUrl.isEmpty())
        {
            return null;
        }

        BannerImagePanel banner = new BannerImagePanel(BANNER_WIDTH, BANNER_HEIGHT);
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        listener.loadImage(assetUrl, BANNER_WIDTH, BANNER_HEIGHT, banner::setImage);
        return banner;
    }

    private JPanel buildStatusIcons(ClanListing clan)
    {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        int column = 0;

        if (clan.isSponsored())
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = column++;
            constraints.gridy = 0;
            constraints.insets = new Insets(0, 0, 0, 5);
            constraints.anchor = GridBagConstraints.WEST;
            row.add(new StatusIconLabel(new StarStatusIcon(), "Featured clan"), constraints);
        }

        if (row.getComponentCount() == 0)
        {
            return null;
        }

        return row;
    }

    private JPanel buildTitleRow(ClanListing clan, boolean clickable)
    {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel statusIcons = buildStatusIcons(clan);
        int iconCount = statusIcons == null ? 0 : statusIcons.getComponentCount();
        int nameWidth = CONTENT_TEXT_WIDTH - (iconCount * 21) - (iconCount > 0 ? 6 : 0);

        GridBagConstraints nameConstraints = new GridBagConstraints();
        nameConstraints.gridx = 0;
        nameConstraints.gridy = 0;
        nameConstraints.weightx = 1;
        nameConstraints.fill = GridBagConstraints.HORIZONTAL;
        nameConstraints.anchor = GridBagConstraints.WEST;
        JLabel nameLabel = buildNameLabel(clan, Math.max(90, nameWidth));
        if (clickable)
        {
            makeClanDetailLink(nameLabel, clan);
        }
        row.add(nameLabel, nameConstraints);

        if (statusIcons != null)
        {
            GridBagConstraints iconConstraints = new GridBagConstraints();
            iconConstraints.gridx = 1;
            iconConstraints.gridy = 0;
            iconConstraints.insets = new Insets(0, 6, 0, 0);
            iconConstraints.anchor = GridBagConstraints.EAST;
            row.add(statusIcons, iconConstraints);
        }

        return row;
    }

    private static JPanel buildMemberRow(ClanListing clan)
    {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(CONTENT_TEXT_WIDTH, 22));

        JLabel label = new JLabel(memberCountText(clan));
        label.setForeground(TEXT_MUTED);
        label.setFont(label.getFont().deriveFont(Font.BOLD, MEMBER_FONT_SIZE));
        label.setToolTipText("Listed member total from ClanFinder website data. This is not live online status.");

        row.add(label, BorderLayout.WEST);

        return row;
    }

    private static JPanel buildMetaRow(ClanListing clan)
    {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(CONTENT_TEXT_WIDTH, 22));

        JLabel region = new JLabel(regionText(clan));
        region.setIcon(regionIcon(clan.getRegion()));
        region.setIconTextGap(5);
        region.setForeground(TEXT_MUTED);
        region.setFont(region.getFont().deriveFont(Font.BOLD, 12f));
        region.setBorder(new EmptyBorder(0, 0, 0, 0));

        GridBagConstraints regionConstraints = new GridBagConstraints();
        regionConstraints.gridx = 0;
        regionConstraints.gridy = 0;
        regionConstraints.anchor = GridBagConstraints.WEST;
        row.add(region, regionConstraints);

        String details = metaDetailText(clan);
        if (!details.isEmpty())
        {
            JLabel detail = new JLabel(details);
            detail.setForeground(TEXT_MUTED);
            detail.setFont(detail.getFont().deriveFont(Font.PLAIN, 12f));

            GridBagConstraints detailConstraints = new GridBagConstraints();
            detailConstraints.gridx = 1;
            detailConstraints.gridy = 0;
            detailConstraints.weightx = 1;
            detailConstraints.anchor = GridBagConstraints.WEST;
            detailConstraints.insets = new Insets(0, 6, 0, 0);
            row.add(detail, detailConstraints);
        }

        return row;
    }

    private static String regionText(ClanListing clan)
    {
        if (!clan.getRegion().isEmpty())
        {
            return clan.getRegion();
        }

        return "Global";
    }

    private static String metaDetailText(ClanListing clan)
    {
        StringBuilder builder = new StringBuilder();

        if (!clan.getTimezone().isEmpty())
        {
            builder.append(clan.getTimezone());
        }

        return builder.toString();
    }

    private JPanel buildTypeBadgeGrid(ClanListing clan)
    {
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(CONTENT_TEXT_WIDTH, Integer.MAX_VALUE));

        for (int i = 0; i < clan.getTypes().size(); i++)
        {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.weightx = 1;
            constraints.insets = new Insets(i == 0 ? 0 : 5, 0, 0, 0);
            constraints.anchor = GridBagConstraints.WEST;
            grid.add(new TypeBadgeLabel(clan.getTypes().get(i)), constraints);
        }

        Dimension preferred = grid.getPreferredSize();
        Dimension fixed = new Dimension(CONTENT_TEXT_WIDTH, preferred.height);
        grid.setPreferredSize(fixed);
        grid.setMinimumSize(fixed);
        grid.setMaximumSize(fixed);
        return grid;
    }

    private static void appendDivider(StringBuilder builder)
    {
        if (builder.length() > 0)
        {
            builder.append(" | ");
        }
    }

    private JLabel buildNameLabel(ClanListing clan, int width)
    {
        JLabel label = buildWrappedLabel(clan.getName(), width, Font.BOLD, TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, CLAN_NAME_FONT_SIZE));
        return label;
    }

    private static JLabel sectionLabel(String value)
    {
        JLabel label = new JLabel(value);
        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, SECTION_LABEL_FONT_SIZE));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private static JLabel buildWrappedLabel(String value, int width, int style, Color color)
    {
        JLabel label = new JLabel("<html><body style='width:" + width + "px'>" + escapeHtml(value) + "</body></html>");
        label.setForeground(color);
        label.setFont(label.getFont().deriveFont(style, 11.5f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(width, Short.MAX_VALUE));
        return label;
    }

    private static JTextArea buildTextBlock(String value, int width, int style, Color color)
    {
        return buildTextBlock(value, width, style, color, 11.5f);
    }

    private static JTextArea buildTextBlock(String value, int width, int style, Color color, float size)
    {
        JTextArea area = new JTextArea(normalizeDisplayText(value));
        area.setOpaque(false);
        area.setEditable(false);
        area.setFocusable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(color);
        area.setFont(area.getFont().deriveFont(style, size));
        area.setBorder(new EmptyBorder(0, 0, 0, 0));
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
        area.setSize(new Dimension(width, Short.MAX_VALUE));

        Dimension preferred = area.getPreferredSize();
        int lineHeight = area.getFontMetrics(area.getFont()).getHeight();
        int height = Math.max(lineHeight, preferred.height + 1);
        Dimension fixed = new Dimension(width, height);
        area.setPreferredSize(fixed);
        area.setMinimumSize(fixed);
        area.setMaximumSize(fixed);
        return area;
    }

    private JPanel buildRequirementsPanel(ClanListing clan, int width)
    {
        JPanel panel = new RoundedPanel(DESCRIPTION_BACKGROUND, DESCRIPTION_BORDER, 7);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(7, 8, 7, 8));

        JLabel label = new JLabel("Requirements");
        label.setForeground(TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, REQUIREMENTS_LABEL_FONT_SIZE));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));

        List<String> requirements = detailRequirementLines(clan);
        String summary = requirements.isEmpty() ? "Open requirements" : String.join(" | ", requirements);
        panel.add(buildTextBlock(summary, width - 16, Font.PLAIN, TEXT_SECONDARY, REQUIREMENTS_FONT_SIZE));

        Dimension preferred = panel.getPreferredSize();
        Dimension fixed = new Dimension(width, preferred.height);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        return panel;
    }

    private JPanel buildDetailRequirementsPanel(ClanListing clan, int width)
    {
        JPanel panel = new RoundedPanel(DESCRIPTION_BACKGROUND, DESCRIPTION_BORDER, 7);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        List<String> requirements = detailRequirementLines(clan);
        if (requirements.isEmpty())
        {
            panel.add(buildTextBlock("No listed requirements.", width - 16, Font.PLAIN, TEXT_SECONDARY, REQUIREMENTS_FONT_SIZE));
        }
        else
        {
            for (int i = 0; i < requirements.size(); i++)
            {
                if (i > 0)
                {
                    panel.add(Box.createVerticalStrut(4));
                }

                panel.add(buildTextBlock(requirements.get(i), width - 16, Font.PLAIN, TEXT_SECONDARY, REQUIREMENTS_FONT_SIZE));
            }
        }

        Dimension preferred = panel.getPreferredSize();
        Dimension fixed = new Dimension(width, preferred.height);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        return panel;
    }

    private void requestSearch()
    {
        Object selectedType = typeSelect.getSelectedItem();
        Object selectedRegion = regionSelect.getSelectedItem();
        String type = selectedType instanceof ClanTypeOption ? ((ClanTypeOption) selectedType).getValue() : "";
        String region = selectedRegion instanceof RegionOption ? ((RegionOption) selectedRegion).getValue() : "";

        currentQuery = new ClanSearchQuery(
            searchField.getText(),
            type,
            region,
            25
        );
        listener.search(currentQuery);
    }

    private void requestNextPage()
    {
        if (loadingMore || displayedCount >= totalCount)
        {
            return;
        }

        currentQuery = currentQuery.withPage(currentPage + 1);
        listener.search(currentQuery);
    }

    private void showCachedResults()
    {
        resultsPanel.removeAll();
        for (ClanListing clan : displayedClans)
        {
            resultsPanel.add(buildClanCard(clan));
            resultsPanel.add(Box.createVerticalStrut(8));
        }

        addSeeMoreButtonIfNeeded();
        statusLabel.setText("Showing " + displayedCount + " of " + totalCount + " approved clans");
        refreshResults();
    }

    private JPanel buildBackButtonRow()
    {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(CARD_WIDTH, 38));

        JButton button = new JButton("< Back to clans");
        styleActionButton(button, ACTION_BACKGROUND);
        button.setFont(button.getFont().deriveFont(Font.BOLD, BACK_BUTTON_FONT_SIZE));
        button.setPreferredSize(new Dimension(CARD_WIDTH, 34));
        button.addActionListener(event -> showCachedResults());
        row.add(button, BorderLayout.CENTER);

        return row;
    }

    private JPanel buildDetailActions(ClanListing clan)
    {
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton copyButton = new JButton("Copy CC");
        styleActionButton(copyButton, ACTION_BACKGROUND);
        copyButton.setMaximumSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        copyButton.setPreferredSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        copyButton.setEnabled(!clan.getClanChatName().isEmpty());
        copyButton.addActionListener(event -> listener.copyClanChat(clan.getClanChatName()));

        JButton openButton = new JButton("Open full profile");
        styleActionButton(openButton, ACTION_PRIMARY);
        openButton.setMaximumSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        openButton.setPreferredSize(new Dimension(CONTENT_ACTION_WIDTH, 30));
        openButton.addActionListener(event -> listener.openClan(clan.getSlug()));

        copyButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        openButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.add(copyButton);
        actions.add(Box.createVerticalStrut(6));
        actions.add(openButton);
        return actions;
    }

    private JPanel buildEventCard(ClanEvent event)
    {
        JPanel card = new RoundedPanel(new Color(34, 35, 38), CARD_BORDER, 7);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(CONTENT_TEXT_WIDTH, Integer.MAX_VALUE));
        card.setBorder(new EmptyBorder(8, 8, 8, 8));

        card.add(buildEventHeader(event));

        card.add(Box.createVerticalStrut(3));
        card.add(buildTextBlock(event.getTitle(), CONTENT_TEXT_WIDTH - 16, Font.BOLD, TEXT_PRIMARY, EVENT_TEXT_FONT_SIZE));

        String when = formatEventTime(event.getStartAt(), event.getTimezone());
        if (!when.isEmpty())
        {
            card.add(Box.createVerticalStrut(3));
            card.add(buildTextBlock(when, CONTENT_TEXT_WIDTH - 16, Font.PLAIN, TEXT_SECONDARY, EVENT_TEXT_FONT_SIZE));
        }

        if (!event.getHost().isEmpty())
        {
            card.add(Box.createVerticalStrut(3));
            card.add(buildTextBlock("Hosted by " + event.getHost(), CONTENT_TEXT_WIDTH - 16, Font.PLAIN, TEXT_MUTED, EVENT_TEXT_FONT_SIZE));
        }

        if (!event.getDescription().isEmpty())
        {
            card.add(Box.createVerticalStrut(5));
            card.add(buildTextBlock(event.getDescription(), CONTENT_TEXT_WIDTH - 16, Font.PLAIN, TEXT_SECONDARY, EVENT_TEXT_FONT_SIZE));
        }

        if (!event.getRequirements().isEmpty())
        {
            card.add(Box.createVerticalStrut(5));
            card.add(buildTextBlock("Requirements: " + event.getRequirements(), CONTENT_TEXT_WIDTH - 16, Font.PLAIN, TEXT_MUTED, EVENT_TEXT_FONT_SIZE));
        }

        Dimension preferred = card.getPreferredSize();
        card.setPreferredSize(new Dimension(CONTENT_TEXT_WIDTH, preferred.height));
        card.setMinimumSize(new Dimension(CONTENT_TEXT_WIDTH, preferred.height));
        card.setMaximumSize(new Dimension(CONTENT_TEXT_WIDTH, Math.max(preferred.height, Short.MAX_VALUE)));
        return card;
    }

    private JPanel buildEventHeader(ClanEvent event)
    {
        JPanel header = new JPanel(new GridBagLayout());
        header.setOpaque(false);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(CONTENT_TEXT_WIDTH - 16, 24));

        String category = event.getCategory().isEmpty() ? "Clan event" : event.getCategory();
        JLabel categoryLabel = new JLabel(category.toUpperCase(Locale.US));
        categoryLabel.setForeground(TEXT_MUTED);
        categoryLabel.setFont(categoryLabel.getFont().deriveFont(Font.BOLD, 9.5f));

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.weightx = 1;
        labelConstraints.anchor = GridBagConstraints.WEST;
        header.add(categoryLabel, labelConstraints);

        return header;
    }

    private JPanel buildEventCountLabel(ClanListing clan, int width)
    {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(width, 26));
        makeClanDetailLink(row, clan);

        JLabel santaHat = new JLabel(santaHatIcon());
        santaHat.setToolTipText("Clan events");
        makeClanDetailLink(santaHat, clan);

        GridBagConstraints santaConstraints = new GridBagConstraints();
        santaConstraints.gridx = 0;
        santaConstraints.gridy = 0;
        santaConstraints.insets = new Insets(0, 0, 0, 4);
        santaConstraints.anchor = GridBagConstraints.WEST;
        row.add(santaHat, santaConstraints);

        JLabel label = buildWrappedLabel(eventCountText(clan), width - 25, Font.BOLD, TEXT_PRIMARY);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 11.5f));
        label.setToolTipText("View clan events");
        makeClanDetailLink(label, clan);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 1;
        labelConstraints.gridy = 0;
        labelConstraints.weightx = 1;
        labelConstraints.anchor = GridBagConstraints.WEST;
        row.add(label, labelConstraints);

        return row;
    }

    private JPanel buildAboutSentencePanel(String about, int width)
    {
        JPanel panel = new RoundedPanel(DESCRIPTION_BACKGROUND, DESCRIPTION_BORDER, 7);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(7, 8, 7, 8));

        panel.add(buildTextBlock(about, width - 16, Font.PLAIN, TEXT_SECONDARY, DESCRIPTION_FONT_SIZE));

        Dimension preferred = panel.getPreferredSize();
        Dimension fixed = new Dimension(width, preferred.height);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        return panel;
    }

    private JPanel buildDetailDescriptionPanel(String description, int width)
    {
        JPanel panel = new RoundedPanel(DESCRIPTION_BACKGROUND, DESCRIPTION_BORDER, 7);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));

        panel.add(buildTextBlock(description, width - 16, Font.PLAIN, TEXT_SECONDARY, DETAIL_DESCRIPTION_FONT_SIZE));

        Dimension preferred = panel.getPreferredSize();
        Dimension fixed = new Dimension(width, preferred.height);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        return panel;
    }

    private void makeClanDetailLink(Component component, ClanListing clan)
    {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent event)
            {
                listener.viewClan(clan);
            }
        });
    }

    private static String formatEventTime(String value, String timezone)
    {
        if (value == null || value.isEmpty())
        {
            return "";
        }

        try
        {
            String formatted = EVENT_TIME_FORMATTER.withZone(ZoneId.systemDefault()).format(Instant.parse(value));
            return timezone == null || timezone.isEmpty() ? formatted : formatted + " " + timezone;
        }
        catch (DateTimeParseException ignored)
        {
            return value;
        }
    }

    private static String summaryText(ClanListing clan)
    {
        String headline = clan.getHeadline().trim();
        if (!headline.isEmpty())
        {
            return truncate(headline, 112);
        }

        return truncate(clan.getDescription().trim(), 112);
    }

    private static String detailDescriptionText(ClanListing clan)
    {
        String description = clan.getDescription().replaceAll("\\s+", " ").trim();
        if (!description.isEmpty())
        {
            return description;
        }

        return clan.getHeadline().replaceAll("\\s+", " ").trim();
    }

    private static String aboutSentenceText(ClanListing clan)
    {
        String text = clan.getDescription().replaceAll("\\s+", " ").trim();
        if (text.isEmpty())
        {
            text = clan.getHeadline().replaceAll("\\s+", " ").trim();
        }

        if (text.isEmpty())
        {
            return "";
        }

        int sentenceEnd = firstSentenceEnd(text);
        if (sentenceEnd > 0 && sentenceEnd <= 170)
        {
            return text.substring(0, sentenceEnd + 1).trim();
        }

        return truncate(text, 156);
    }

    private static String normalizeDisplayText(String value)
    {
        return value == null ? "" : value.replaceAll("\\s+", " ").trim();
    }

    private static int firstSentenceEnd(String value)
    {
        int first = -1;
        for (char marker : new char[] {'.', '!', '?'})
        {
            int index = value.indexOf(marker);
            if (index >= 45 && (first < 0 || index < first))
            {
                first = index;
            }
        }

        return first;
    }

    private static String requirementsText(ClanListing clan)
    {
        ClanRequirements requirements = clan.getRequirements();
        StringBuilder builder = new StringBuilder();

        if (requirements.getCombatLevel() > 0)
        {
            builder.append("Combat ").append(requirements.getCombatLevel()).append("+");
        }

        if (requirements.getTotalLevel() > 0)
        {
            if (builder.length() > 0)
            {
                builder.append(" | ");
            }
            builder.append("Total ").append(requirements.getTotalLevel()).append("+");
        }

        if (requirements.getQuestPoints() > 0)
        {
            if (builder.length() > 0)
            {
                builder.append(" | ");
            }
            builder.append(requirements.getQuestPoints()).append("+ Quest Points");
        }

        if (requirements.getRaidKc() > 0)
        {
            if (builder.length() > 0)
            {
                builder.append(" | ");
            }
            builder.append(requirements.getRaidKc()).append("+ Raid KC");
        }

        return builder.toString();
    }

    private static boolean hasRequirements(ClanListing clan)
    {
        ClanRequirements requirements = clan.getRequirements();
        return !requirementsText(clan).isEmpty()
            || requirements.isDiscordRequired()
            || requirements.isApplicationRequired()
            || requirements.isMicrophoneRequired();
    }

    private static List<String> detailRequirementLines(ClanListing clan)
    {
        List<String> lines = new ArrayList<>();
        ClanRequirements requirements = clan.getRequirements();

        if (requirements.getCombatLevel() > 0)
        {
            lines.add("Combat level " + requirements.getCombatLevel() + "+");
        }

        if (requirements.getTotalLevel() > 0)
        {
            lines.add("Total level " + requirements.getTotalLevel() + "+");
        }

        if (requirements.getQuestPoints() > 0)
        {
            lines.add("Quest points " + requirements.getQuestPoints() + "+");
        }

        if (requirements.getRaidKc() > 0)
        {
            lines.add("Raid KC " + requirements.getRaidKc() + "+");
        }

        if (requirements.isDiscordRequired())
        {
            lines.add("Discord required");
        }

        if (requirements.isApplicationRequired())
        {
            lines.add("Application required");
        }

        if (requirements.isMicrophoneRequired())
        {
            lines.add("Microphone required");
        }

        return lines;
    }

    private static String eventCountText(ClanListing clan)
    {
        int count = Math.max(clan.getPublicEventCount(), clan.getEvents().size());
        return count + (count == 1 ? " Event" : " Events");
    }

    private static String memberCountText(ClanListing clan)
    {
        int count = Math.max(0, clan.getMemberCount());
        return count + (count == 1 ? " listed member" : " listed members");
    }

    private static boolean hasEvents(ClanListing clan)
    {
        return Math.max(clan.getPublicEventCount(), clan.getEvents().size()) > 0;
    }

    private static String bannerAssetUrl(ClanListing clan)
    {
        if (!clan.getBannerThumbnailUrl().isEmpty())
        {
            return clan.getBannerThumbnailUrl();
        }

        String bannerUrl = clan.getBannerUrl();
        if (bannerUrl.startsWith("/") || bannerUrl.startsWith("https://") || bannerUrl.startsWith("http://"))
        {
            String lower = bannerUrl.toLowerCase();
            if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png"))
            {
                return bannerUrl;
            }
        }

        return "";
    }

    private static JPanel buildStateCard(String title, String body)
    {
        JPanel card = new RoundedPanel(DESCRIPTION_BACKGROUND, DESCRIPTION_BORDER, 8);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12.5f));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titleLabel);

        if (body != null && !body.trim().isEmpty())
        {
            card.add(Box.createVerticalStrut(5));
            card.add(buildTextBlock(body, CARD_WIDTH - 24, Font.PLAIN, TEXT_SECONDARY, 12.8f));
        }

        Dimension preferred = card.getPreferredSize();
        Dimension fixed = new Dimension(CARD_WIDTH, preferred.height);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);
        return card;
    }

    private static void styleField(JTextField field)
    {
        field.setBackground(FIELD_BACKGROUND);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER),
            new EmptyBorder(6, 8, 6, 8)
        ));
    }

    private static void styleCombo(JComboBox<?> combo)
    {
        combo.setBackground(FIELD_BACKGROUND);
        combo.setForeground(TEXT_PRIMARY);
        combo.setBorder(BorderFactory.createLineBorder(CARD_BORDER));
        combo.setPreferredSize(new Dimension(10, 30));
    }

    private static void styleActionButton(JButton button, Color background)
    {
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(TEXT_PRIMARY);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 11f));
        button.setBorder(new EmptyBorder(7, 8, 7, 8));
        button.setOpaque(true);
    }

    private static String truncate(String value, int maxLength)
    {
        if (value.length() <= maxLength)
        {
            return value;
        }

        return value.substring(0, Math.max(0, maxLength - 3)).trim() + "...";
    }

    private void refreshResults()
    {
        resultsPanel.revalidate();
        resultsPanel.repaint();
        Component parent = resultsPanel.getParent();
        if (parent != null)
        {
            parent.revalidate();
            parent.repaint();
        }
        revalidate();
        repaint();
    }

    private void addSeeMoreButtonIfNeeded()
    {
        removeSeeMoreButton();
        if (displayedCount <= 0 || displayedCount >= totalCount)
        {
            return;
        }

        JButton seeMoreButton = new JButton("See more clans", new ReloadIcon());
        seeMoreButton.setName("see-more-clans");
        styleActionButton(seeMoreButton, ACTION_PRIMARY);
        seeMoreButton.setIconTextGap(7);
        seeMoreButton.setMaximumSize(new Dimension(SEE_MORE_BUTTON_WIDTH, 32));
        seeMoreButton.setPreferredSize(new Dimension(SEE_MORE_BUTTON_WIDTH, 32));
        seeMoreButton.addActionListener(event -> requestNextPage());

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setName("see-more-clans-wrapper");
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(CARD_WIDTH, 42));
        wrapper.setPreferredSize(new Dimension(CARD_WIDTH, 42));
        wrapper.setBorder(new EmptyBorder(2, 0, 8, 0));
        wrapper.add(seeMoreButton);

        resultsPanel.add(wrapper);
    }

    private void removeSeeMoreButton()
    {
        for (int i = resultsPanel.getComponentCount() - 1; i >= 0; i--)
        {
            Component component = resultsPanel.getComponent(i);
            if ("see-more-clans-wrapper".equals(component.getName()))
            {
                resultsPanel.remove(i);
            }
        }
    }

    private void removeLoadingMoreMessage()
    {
        for (int i = resultsPanel.getComponentCount() - 1; i >= 0; i--)
        {
            Component component = resultsPanel.getComponent(i);
            if ("loading-more-clans".equals(component.getName()))
            {
                resultsPanel.remove(i);
            }
        }
    }

    private static String escapeHtml(String value)
    {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    private static Icon typeIcon(String type, int size)
    {
        String resource = typeIconResource(type);
        if (resource.isEmpty())
        {
            return null;
        }

        try
        {
            BufferedImage image = loadResourceImage(resource);
            if (image == null)
            {
                return null;
            }

            return new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_SMOOTH));
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private static Icon worldFlagIcon(String resource)
    {
        try
        {
            BufferedImage image = ImageUtil.loadImageResource(WorldHopperPlugin.class, resource);
            return new ImageIcon(image);
        }
        catch (Exception ignored)
        {
            return new GlobeRegionIcon();
        }
    }

    private static Icon regionIcon(String region)
    {
        switch (canonicalRegion(region))
        {
            case "United States":
                return worldFlagIcon("flag_us.png");
            case "United Kingdom":
                return worldFlagIcon("flag_uk.png");
            case "Germany":
                return worldFlagIcon("flag_ger.png");
            case "Australia":
                return worldFlagIcon("flag_aus.png");
            case "Brazil":
                return worldFlagIcon("flag_br.png");
            default:
                return new GlobeRegionIcon();
        }
    }

    private static String canonicalRegion(String region)
    {
        switch (region)
        {
            case "US":
            case "USA":
            case "United States":
            case "United States of America":
                return "United States";
            case "UK":
            case "U.K.":
            case "United Kingdom":
                return "United Kingdom";
            case "Europe":
            case "EU":
            case "Germany":
                return "Germany";
            case "AU / NZ":
            case "AUS":
            case "Australia":
            case "Asia-Pacific":
                return "Australia";
            case "South America":
            case "Brazil":
                return "Brazil";
            case "Other":
                return "Other";
            default:
                return "Global";
        }
    }

    private static String typeIconResource(String type)
    {
        switch (type)
        {
            case "Social":
                return "/game-icons/social.png";
            case "PvM":
                return "/game-icons/pvm.png";
            case "Raids":
                return "/game-icons/raids.png";
            case "Skilling":
                return "/game-icons/skills.png";
            case "Merchants":
                return "/game-icons/grand-exchange.png";
            case "Ironman":
                return "/game-icons/ironman.png";
            case "Hardcore Ironman":
                return "/game-icons/hardcore-ironman.png";
            case "PvP":
                return "/game-icons/pvp.png";
            case "Minigames":
                return "/game-icons/minigames.png";
            case "Bossing":
                return "/game-icons/bossing.png";
            case "Collection Log":
                return "/game-icons/collection-log.png";
            case "New Player":
                return "/game-icons/adventure-paths.png";
            case "Completionist":
                return "/game-icons/completionist.png";
            case "F2P":
                return "/game-icons/free-to-play-star.png";
            default:
                return "";
        }
    }

    private static String compactTypeName(String type)
    {
        switch (type)
        {
            case "Hardcore Ironman":
                return "Hardcore";
            case "Collection Log":
                return "Collection";
            case "Completionist":
                return "Complete";
            default:
                return type;
        }
    }

    interface ClanFinderPanelListener
    {
        void search(ClanSearchQuery query);

        void copyClanChat(String clanChatName);

        void openClan(String slug);

        void viewClan(ClanListing clan);

        String resolveAssetUrl(String assetUrl);

        void loadImage(String assetUrl, int width, int height, Consumer<BufferedImage> callback);
    }

    private static final class BannerImagePanel extends JPanel
    {
        private final int width;
        private final int height;
        private BufferedImage image;
        private boolean loaded;

        private BannerImagePanel(int width, int height)
        {
            this.width = width;
            this.height = height;
            setOpaque(false);
            setPreferredSize(new Dimension(width, height));
            setMinimumSize(new Dimension(width, height));
            setMaximumSize(new Dimension(width, height));
        }

        void setImage(BufferedImage image)
        {
            if (!SwingUtilities.isEventDispatchThread())
            {
                SwingUtilities.invokeLater(() -> setImage(image));
                return;
            }

            this.image = image;
            this.loaded = true;
            revalidate();
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            try
            {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, width, height, 8, 8);
                g.setClip(shape);

                g.setColor(new Color(47, 49, 53));
                g.fillRect(0, 0, width, height);

                if (image != null)
                {
                    g.drawImage(image, 0, 0, width, height, null);
                    g.setColor(new Color(0, 0, 0, 68));
                    g.fillRect(0, 0, width, height);
                }
                else if (!loaded)
                {
                    g.setColor(new Color(64, 66, 72));
                    g.fillRoundRect(12, height / 2 - 5, width - 24, 10, 8, 8);
                }

                g.setClip(null);
                g.setColor(CARD_BORDER);
                g.draw(shape);
            }
            finally
            {
                g.dispose();
            }
        }
    }

    private static final class ClanTypeOption
    {
        private final String label;
        private final String value;
        private final Icon icon;

        private ClanTypeOption(String label, String value, Icon icon)
        {
            this.label = label;
            this.value = value;
            this.icon = icon;
        }

        String getValue()
        {
            return value;
        }

        Icon getIcon()
        {
            return icon;
        }

        @Override
        public String toString()
        {
            return label;
        }
    }

    private static final class ClanTypeOptionRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
        )
        {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(4, 6, 4, 6));
            label.setIconTextGap(8);

            if (value instanceof ClanTypeOption)
            {
                ClanTypeOption option = (ClanTypeOption) value;
                label.setText(option.toString());
                label.setIcon(option.getIcon());
            }

            return label;
        }
    }

    private static final class RegionOption
    {
        private final String label;
        private final String value;
        private final Icon icon;

        private RegionOption(String label, String value, Icon icon)
        {
            this.label = label;
            this.value = value;
            this.icon = icon;
        }

        String getValue()
        {
            return value;
        }

        Icon getIcon()
        {
            return icon;
        }

        @Override
        public String toString()
        {
            return label;
        }
    }

    private static final class RegionOptionRenderer extends DefaultListCellRenderer
    {
        @Override
        public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus
        )
        {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setBorder(new EmptyBorder(4, 6, 4, 6));
            label.setIconTextGap(8);

            if (value instanceof RegionOption)
            {
                RegionOption option = (RegionOption) value;
                label.setText(option.toString());
                label.setIcon(option.getIcon());
            }

            return label;
        }
    }

    private static final class AllTypesIcon implements Icon
    {
        private static final int SIZE = 18;

        @Override
        public int getIconWidth()
        {
            return SIZE;
        }

        @Override
        public int getIconHeight()
        {
            return SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);
            g.setColor(new Color(108, 116, 128, 46));
            g.fillRoundRect(0, 0, SIZE, SIZE, 5, 5);
            g.setColor(ICON_NEUTRAL);
            g.fill(new Ellipse2D.Double(4, 4, 4, 4));
            g.fill(new Ellipse2D.Double(10, 4, 4, 4));
            g.fill(new Ellipse2D.Double(4, 10, 4, 4));
            g.fill(new Ellipse2D.Double(10, 10, 4, 4));
            g.dispose();
        }
    }

    private static final class GlobeRegionIcon implements Icon
    {
        private static final int SIZE = 18;

        @Override
        public int getIconWidth()
        {
            return SIZE;
        }

        @Override
        public int getIconHeight()
        {
            return SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);

            g.setColor(new Color(108, 116, 128, 48));
            g.fillOval(0, 0, SIZE - 1, SIZE - 1);
            g.setColor(new Color(128, 148, 170));
            g.drawOval(2, 2, SIZE - 5, SIZE - 5);
            g.drawArc(5, 2, 8, SIZE - 5, 90, 180);
            g.drawArc(5, 2, 8, SIZE - 5, -90, 180);
            g.drawLine(3, 9, SIZE - 4, 9);
            g.setColor(new Color(80, 92, 106));
            g.drawOval(0, 0, SIZE - 1, SIZE - 1);
            g.dispose();
        }
    }

    private static final class ReloadIcon implements Icon
    {
        private static final int SIZE = 14;

        @Override
        public int getIconWidth()
        {
            return SIZE;
        }

        @Override
        public int getIconHeight()
        {
            return SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            try
            {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.translate(x, y);
                g.setStroke(new java.awt.BasicStroke(1.8f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g.setColor(TEXT_PRIMARY);
                g.drawArc(2, 2, 10, 10, 35, 285);
                g.drawLine(11, 1, 11, 5);
                g.drawLine(11, 1, 7, 1);
            }
            finally
            {
                g.dispose();
            }
        }
    }

    private static final class StatusIconLabel extends JLabel
    {
        private static final int SIZE = 16;

        private StatusIconLabel(Icon icon, String tooltip)
        {
            super(icon);
            setPreferredSize(new Dimension(SIZE, SIZE));
            setMinimumSize(new Dimension(SIZE, SIZE));
            setMaximumSize(new Dimension(SIZE, SIZE));
            setToolTipText(tooltip);
            setOpaque(false);
        }
    }

    private static final class StarStatusIcon implements Icon
    {
        private static final int SIZE = 16;

        @Override
        public int getIconWidth()
        {
            return SIZE;
        }

        @Override
        public int getIconHeight()
        {
            return SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);

            g.setColor(new Color(76, 60, 32));
            g.fillRoundRect(0, 0, SIZE - 1, SIZE - 1, 4, 4);
            g.setColor(new Color(150, 115, 47));
            g.drawRoundRect(0, 0, SIZE - 1, SIZE - 1, 4, 4);

            int centerX = SIZE / 2;
            int centerY = SIZE / 2;
            int outer = 5;
            int inner = 2;
            int[] xs = new int[10];
            int[] ys = new int[10];
            for (int i = 0; i < 10; i++)
            {
                double angle = Math.toRadians(-90 + i * 36);
                int radius = i % 2 == 0 ? outer : inner;
                xs[i] = centerX + (int) Math.round(Math.cos(angle) * radius);
                ys[i] = centerY + (int) Math.round(Math.sin(angle) * radius);
            }

            g.setColor(new Color(242, 196, 91));
            g.fillPolygon(xs, ys, xs.length);
            g.dispose();
        }
    }

    private static final class DiscordRequiredLabel extends JLabel
    {
        private static final int WIDTH = 124;
        private static final int HEIGHT = 18;

        private DiscordRequiredLabel()
        {
            super("Discord required", discordIcon(), JLabel.LEFT);
            setIconTextGap(6);
            setForeground(new Color(218, 224, 255));
            setFont(getFont().deriveFont(Font.BOLD, 10.5f));
            setBorder(new EmptyBorder(0, 0, 0, 0));
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setMinimumSize(new Dimension(WIDTH, HEIGHT));
            setMaximumSize(new Dimension(WIDTH, HEIGHT));
            setToolTipText("Discord required");
            setOpaque(false);
        }
    }

    private static Icon discordIcon()
    {
        try
        {
            BufferedImage image = loadResourceImage("/discord-logo.png");
            return image == null ? null : new ImageIcon(image);
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private static Icon santaHatIcon()
    {
        try
        {
            BufferedImage image = loadResourceImage("/santa-hat.png");
            return image == null ? null : new ImageIcon(image.getScaledInstance(SANTA_HAT_WIDTH, SANTA_HAT_HEIGHT, Image.SCALE_SMOOTH));
        }
        catch (Exception ignored)
        {
            return null;
        }
    }

    private static BufferedImage loadResourceImage(String resource) throws Exception
    {
        try (InputStream stream = ClanFinderPanel.class.getResourceAsStream(resource))
        {
            return stream == null ? null : ImageIO.read(stream);
        }
    }

    private static final class VerifiedStatusIcon implements Icon
    {
        private static final int SIZE = 16;

        @Override
        public int getIconWidth()
        {
            return SIZE;
        }

        @Override
        public int getIconHeight()
        {
            return SIZE;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.translate(x, y);

            g.setColor(new Color(38, 60, 48));
            g.fillRoundRect(0, 0, SIZE - 1, SIZE - 1, 4, 4);
            g.setColor(new Color(73, 125, 89));
            g.drawRoundRect(0, 0, SIZE - 1, SIZE - 1, 4, 4);

            g.setStroke(new java.awt.BasicStroke(1.9f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            g.setColor(new Color(174, 222, 186));
            g.drawLine(4, 8, 7, 11);
            g.drawLine(7, 11, 12, 5);
            g.dispose();
        }
    }

    private static final class TypeBadgeLabel extends JLabel
    {
        private TypeBadgeLabel(String type)
        {
            super(compactTypeName(type));
            setIcon(typeIcon(type, TYPE_BADGE_ICON_SIZE));
            setIconTextGap(6);
            setForeground(new Color(196, 207, 218));
            setFont(getFont().deriveFont(Font.BOLD, TYPE_BADGE_FONT_SIZE));
            setBorder(new EmptyBorder(4, 7, 4, 7));
            setPreferredSize(new Dimension(TYPE_BADGE_WIDTH, TYPE_BADGE_HEIGHT));
            setMinimumSize(new Dimension(TYPE_BADGE_WIDTH, TYPE_BADGE_HEIGHT));
            setMaximumSize(new Dimension(TYPE_BADGE_WIDTH, TYPE_BADGE_HEIGHT));
            setToolTipText(type);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
            g.setColor(CHIP_BACKGROUND);
            g.fill(shape);
            g.setColor(CHIP_BORDER);
            g.draw(shape);
            g.dispose();

            super.paintComponent(graphics);
        }
    }

    private static final class RoundedPanel extends JPanel
    {
        private final Color background;
        private final Color border;
        private final int radius;

        private RoundedPanel(Color background, Color border, int radius)
        {
            this.background = background;
            this.border = border;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g.setColor(background);
            g.fill(shape);
            g.setColor(border);
            g.draw(shape);
            g.dispose();

            super.paintComponent(graphics);
        }
    }

    private static final class PillLabel extends JLabel
    {
        private final Color background;
        private final Color border;

        private PillLabel(String text, Color background, Color border, Color foreground)
        {
            super(text);
            this.background = background;
            this.border = border;
            setForeground(foreground);
            setFont(getFont().deriveFont(Font.BOLD, 10f));
            setBorder(new EmptyBorder(3, 6, 3, 6));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            RoundRectangle2D shape = new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 9, 9);
            g.setColor(background);
            g.fill(shape);
            g.setColor(border);
            g.draw(shape);
            g.dispose();

            super.paintComponent(graphics);
        }
    }

    private static final class PlaceholderTextField extends JTextField
    {
        private final String placeholder;

        private PlaceholderTextField(String placeholder)
        {
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics graphics)
        {
            super.paintComponent(graphics);

            if (!getText().isEmpty() || isFocusOwner())
            {
                return;
            }

            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setColor(new Color(140, 140, 140));
            Insets insets = getInsets();
            int y = (getHeight() - g.getFontMetrics().getHeight()) / 2 + g.getFontMetrics().getAscent();
            g.drawString(placeholder, insets.left + 2, y);
            g.dispose();
        }
    }

    private static String initials(String value)
    {
        String cleaned = value == null ? "" : value.trim();
        if (cleaned.isEmpty())
        {
            return "?";
        }

        String[] parts = cleaned.split("\\s+");
        if (parts.length == 1)
        {
            return parts[0].substring(0, Math.min(parts[0].length(), 2)).toUpperCase();
        }

        return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
    }
}
