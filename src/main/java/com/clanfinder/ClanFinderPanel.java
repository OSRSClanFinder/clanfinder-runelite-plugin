package com.clanfinder;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
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
import java.awt.Polygon;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
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
    private static final Color ACTION_SUCCESS = new Color(61, 111, 78);
    private static final Color ACTION_DISCORD = new Color(88, 101, 242);
    private static final Pattern DECIMAL_HTML_ENTITY_PATTERN = Pattern.compile("&#(\\d{1,7});");
    private static final Pattern HEX_HTML_ENTITY_PATTERN = Pattern.compile("&#x([0-9a-fA-F]{1,6});");
    private static final String[] EMOJI_FONT_CANDIDATES = {
        "Apple Color Emoji",
        "Segoe UI Emoji",
        "Noto Color Emoji",
        "Apple Symbols",
        Font.DIALOG
    };
    private static final String EMOJI_FONT_FAMILY = findEmojiFontFamily();
    private static final Map<String, Icon> EMOJI_ICON_CACHE = new HashMap<>();
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
    private final JPanel controlsWrapper;
    private final JPanel resultsPanel = new JPanel();
    private final JScrollPane resultsScrollPane;
    private JPanel controlsPanel;
    private JPanel searchControlsPanel;
    private JButton searchToggleButton;
    private final List<ClanListing> displayedClans = new ArrayList<>();
    private ClanSearchQuery currentQuery = new ClanSearchQuery("", "", "", 25);
    private int currentPage = 1;
    private int displayedCount;
    private int totalCount;
    private boolean loadingMore;
    private boolean searchControlsExpanded = true;

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

        controlsWrapper = buildControlsWrapper();
        add(controlsWrapper, BorderLayout.NORTH);
        resultsScrollPane = new JScrollPane(resultsPanel);
        resultsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        resultsScrollPane.setBackground(PANEL_BACKGROUND);
        resultsScrollPane.setOpaque(true);
        resultsScrollPane.getViewport().setBackground(PANEL_BACKGROUND);
        resultsScrollPane.getViewport().setOpaque(true);
        resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(resultsScrollPane, BorderLayout.CENTER);
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 10.5f));
        statusLabel.setBorder(new EmptyBorder(2, 2, 0, 2));
        add(statusLabel, BorderLayout.SOUTH);
    }

    void showLoading()
    {
        setControlsVisible(true);
        loadingMore = false;
        currentPage = 1;
        displayedCount = 0;
        totalCount = 0;
        displayedClans.clear();
        statusLabel.setText("Loading approved clans...");
        resultsPanel.removeAll();
        resultsPanel.add(buildStateCard(
            "Loading clans",
            "Fetching approved listings from Clan Finder."
        ));
        refreshResults(true);
    }

    void showLoadingMore()
    {
        setControlsVisible(true);
        loadingMore = true;
        removeSeeMoreButton();
        statusLabel.setText("Loading more approved clans...");
        JPanel loadingCard = buildStateCard(
            "Loading more clans",
            "Fetching the next page of approved listings."
        );
        loadingCard.setName("loading-more-clans");
        resultsPanel.add(loadingCard);
        refreshResults(false);
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
        refreshResults(!append);
    }

    void showCachedResults(ClanSearchResponse response, String statusText)
    {
        showResults(response);
        statusLabel.setText(statusText);
    }

    void showClanDetailLoading(ClanListing clan)
    {
        setControlsVisible(true);
        loadingMore = false;
        statusLabel.setText("Loading " + clan.getName() + " events...");
        resultsPanel.removeAll();
        resultsPanel.add(buildBackButtonRow());
        resultsPanel.add(Box.createVerticalStrut(8));
        resultsPanel.add(buildStateCard(
            "Loading clan details",
            "Fetching the latest profile and event details from Clan Finder."
        ));
        refreshResults(true);
    }

    void showClanDetail(ClanListing clan)
    {
        showClanDetail(clan, "");
    }

    void showClanDetail(ClanListing clan, String notice)
    {
        setControlsVisible(true);
        loadingMore = false;
        statusLabel.setText(clan.getName() + " events");
        resultsPanel.removeAll();
        resultsPanel.add(buildBackButtonRow());
        resultsPanel.add(Box.createVerticalStrut(8));
        resultsPanel.add(buildClanDetailPage(clan, notice));
        refreshResults(true);
    }

    void showAddClanPage()
    {
        setControlsVisible(false);
        loadingMore = false;
        statusLabel.setText("Add a clan");
        resultsPanel.removeAll();
        resultsPanel.add(buildBackButtonRow());
        resultsPanel.add(Box.createVerticalStrut(8));
        resultsPanel.add(buildAddClanPage());
        refreshResults(true);
    }

    void showLoadMoreError()
    {
        loadingMore = false;
        removeLoadingMoreMessage();
        addSeeMoreButtonIfNeeded();
        statusLabel.setText("Could not load more clans");
        refreshResults(false);
    }

    void showError(String message)
    {
        statusLabel.setText("Unable to load Clan Finder");
        resultsPanel.removeAll();
        resultsPanel.add(buildStateCard("Unable to load clans", message));
        refreshResults(true);
    }

    private JPanel buildControls()
    {
        JPanel controls = new JPanel(new GridBagLayout());
        controlsPanel = controls;
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

        JPanel titleRow = new JPanel(new BorderLayout(6, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Clan Finder");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16.2f));
        title.setBorder(new EmptyBorder(0, 0, 2, 0));
        titleRow.add(title, BorderLayout.WEST);

        searchToggleButton = new JButton(new MinusIcon());
        searchToggleButton.setName("search-toggle-button");
        styleToggleButton(searchToggleButton);
        searchToggleButton.addActionListener(event -> setSearchControlsExpanded(!searchControlsExpanded));
        titleRow.add(searchToggleButton, BorderLayout.EAST);

        controls.add(titleRow, constraints);

        searchControlsPanel = buildSearchControlsPanel();
        searchControlsPanel.setName("search-controls-panel");

        constraints.gridy++;
        controls.add(searchControlsPanel, constraints);

        updateControlsPanelSize();
        return controls;
    }

    private JPanel buildSearchControlsPanel()
    {
        JPanel controls = new JPanel(new GridBagLayout());
        controls.setBackground(PANEL_BACKGROUND);
        controls.setOpaque(true);

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

        JButton addClanButton = new JButton("Add Clan", new PlusIcon());
        styleActionButton(addClanButton, ACTION_SUCCESS);
        addClanButton.setIconTextGap(7);
        addClanButton.addActionListener(event -> showAddClanPage());

        constraints.gridy++;
        controls.add(addClanButton, constraints);

        searchField.addActionListener(event -> requestSearch());
        typeSelect.addActionListener(event -> requestSearch());
        regionSelect.addActionListener(event -> requestSearch());
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

    private void setSearchControlsExpanded(boolean expanded)
    {
        searchControlsExpanded = expanded;
        if (searchControlsPanel != null)
        {
            searchControlsPanel.setVisible(expanded);
        }
        if (searchToggleButton != null)
        {
            searchToggleButton.setIcon(expanded ? new MinusIcon() : new PlusIcon());
            searchToggleButton.setToolTipText(expanded ? "Hide search controls" : "Show search controls");
        }

        updateControlsPanelSize();
        if (controlsWrapper != null)
        {
            controlsWrapper.revalidate();
            controlsWrapper.repaint();
        }
        revalidate();
        repaint();
    }

    private void updateControlsPanelSize()
    {
        if (controlsPanel == null)
        {
            return;
        }

        controlsPanel.setPreferredSize(null);
        controlsPanel.setMinimumSize(null);
        controlsPanel.setMaximumSize(null);
        Dimension preferred = controlsPanel.getPreferredSize();
        Dimension fixed = new Dimension(CARD_WIDTH, preferred.height);
        controlsPanel.setPreferredSize(fixed);
        controlsPanel.setMinimumSize(fixed);
        controlsPanel.setMaximumSize(fixed);
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

    private JPanel buildAddClanPage()
    {
        JPanel page = new RoundedPanel(CARD_BACKGROUND, CARD_BORDER, 8);
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setAlignmentX(Component.CENTER_ALIGNMENT);
        page.setMaximumSize(new Dimension(CARD_WIDTH, Integer.MAX_VALUE));
        page.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Add Your Clan");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16.4f));

        JLabel subtitle = new JLabel("Clan Finder listing");
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitle.setForeground(TEXT_MUTED);
        subtitle.setFont(subtitle.getFont().deriveFont(Font.BOLD, 10.8f));

        JPanel message = new RoundedPanel(DESCRIPTION_BACKGROUND, DESCRIPTION_BORDER, 7);
        message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
        message.setAlignmentX(Component.LEFT_ALIGNMENT);
        message.setBorder(new EmptyBorder(10, 10, 10, 10));
        message.add(buildTextBlock(
            "Join OSRS' best clan directory. Share your clan with players looking for the right community, events, and goals.",
            CONTENT_TEXT_WIDTH - 20,
            Font.PLAIN,
            TEXT_SECONDARY,
            BODY_TEXT_FONT_SIZE
        ));
        message.add(Box.createVerticalStrut(8));
        message.add(buildTextBlock(
            "To have your clan listed on Clan Finder, simply list your clan on the site.",
            CONTENT_TEXT_WIDTH - 20,
            Font.PLAIN,
            TEXT_SECONDARY,
            BODY_TEXT_FONT_SIZE
        ));
        lockPanelWidth(message, CONTENT_TEXT_WIDTH);

        JButton listButton = new JButton("List Clan on Site", new GlobeRegionIcon());
        styleActionButton(listButton, ACTION_PRIMARY);
        listButton.setIconTextGap(7);
        listButton.setMaximumSize(new Dimension(CONTENT_ACTION_WIDTH, 32));
        listButton.setPreferredSize(new Dimension(CONTENT_ACTION_WIDTH, 32));
        listButton.addActionListener(event -> listener.openClanRegistration());

        JButton discordButton = new JButton("Open Discord Invite", discordIcon());
        styleActionButton(discordButton, ACTION_DISCORD);
        discordButton.setIconTextGap(7);
        discordButton.setMaximumSize(new Dimension(CONTENT_ACTION_WIDTH, 32));
        discordButton.setPreferredSize(new Dimension(CONTENT_ACTION_WIDTH, 32));
        discordButton.addActionListener(event -> listener.openSupportDiscord());

        listButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        discordButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        page.add(title);
        page.add(Box.createVerticalStrut(3));
        page.add(subtitle);
        page.add(Box.createVerticalStrut(10));
        page.add(message);
        page.add(Box.createVerticalStrut(10));
        page.add(listButton);
        page.add(Box.createVerticalStrut(6));
        page.add(discordButton);

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
        label.setToolTipText("Listed member total from Clan Finder website data. This is not live online status.");

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
        JLabel label = new JLabel("<html><body style='width:" + width + "px'>" + escapeHtml(normalizeDisplayText(value)) + "</body></html>");
        label.setForeground(color);
        label.setFont(displayTextFont(style, 11.5f));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(width, Short.MAX_VALUE));
        return label;
    }

    private static JTextPane buildTextBlock(String value, int width, int style, Color color)
    {
        return buildTextBlock(value, width, style, color, 11.5f);
    }

    private static JTextPane buildTextBlock(String value, int width, int style, Color color, float size)
    {
        Font textFont = displayTextFont(style, size);
        JTextPane pane = new WrappingTextPane(width);
        pane.setEditorKit(new WrappingStyledEditorKit());
        pane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        ((DefaultCaret) pane.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        pane.setOpaque(false);
        pane.setEditable(false);
        pane.setFocusable(false);
        pane.setForeground(color);
        pane.setFont(textFont);
        pane.setBorder(new EmptyBorder(0, 0, 0, 0));
        pane.setAlignmentX(Component.LEFT_ALIGNMENT);
        insertStyledText(
            pane.getStyledDocument(),
            addSoftBreaks(normalizeMultilineDisplayText(value)),
            textAttributes(textFont.getFamily(), style, size, color),
            textAttributes(EMOJI_FONT_FAMILY, Font.PLAIN, size, color)
        );
        pane.setCaretPosition(0);

        pane.setSize(new Dimension(width, Integer.MAX_VALUE));
        Dimension preferred = pane.getPreferredSize();
        int lineHeight = pane.getFontMetrics(textFont).getHeight();
        int height = Math.max(lineHeight, preferred.height + Math.max(2, lineHeight / 4));
        Dimension fixed = new Dimension(width, height);
        pane.setPreferredSize(fixed);
        pane.setMinimumSize(fixed);
        pane.setMaximumSize(fixed);
        return pane;
    }

    private static String addSoftBreaks(String value)
    {
        StringBuilder builder = new StringBuilder(value.length() + 16);
        int unbroken = 0;
        for (int offset = 0; offset < value.length();)
        {
            int codePoint = value.codePointAt(offset);
            builder.appendCodePoint(codePoint);

            if (Character.isWhitespace(codePoint) || codePoint == '\n')
            {
                unbroken = 0;
            }
            else
            {
                unbroken++;
                if (codePoint == '/' || codePoint == '.' || codePoint == '-' || codePoint == '_' || codePoint == ':' || unbroken >= 18)
                {
                    builder.append('\u200B');
                    unbroken = 0;
                }
            }

            offset += Character.charCount(codePoint);
        }

        return builder.toString();
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

        List<String> requirements = summaryRequirementLines(clan);
        if (requirements.isEmpty())
        {
            panel.add(buildRequirementBullet("Open requirements", width - 16));
        }
        else
        {
            for (int i = 0; i < requirements.size(); i++)
            {
                if (i > 0)
                {
                    panel.add(Box.createVerticalStrut(3));
                }

                panel.add(buildRequirementBullet(requirements.get(i), width - 16));
            }
        }

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

                panel.add(buildRequirementBullet(requirements.get(i), width - 16));
            }
        }

        Dimension preferred = panel.getPreferredSize();
        Dimension fixed = new Dimension(width, preferred.height);
        panel.setPreferredSize(fixed);
        panel.setMinimumSize(fixed);
        panel.setMaximumSize(fixed);
        return panel;
    }

    private static JPanel buildRequirementBullet(String value, int width)
    {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dot = new JLabel(new RequirementDotIcon());
        dot.setPreferredSize(new Dimension(12, 16));
        dot.setMinimumSize(new Dimension(12, 16));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.insets = new Insets(4, 0, 0, 5);
        row.add(dot, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 0, 0);
        row.add(buildTextBlock(value, width - 17, Font.PLAIN, TEXT_SECONDARY, REQUIREMENTS_FONT_SIZE), constraints);

        Dimension preferred = row.getPreferredSize();
        Dimension fixed = new Dimension(width, preferred.height);
        row.setPreferredSize(fixed);
        row.setMinimumSize(fixed);
        row.setMaximumSize(fixed);
        return row;
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
        setControlsVisible(true);
        resultsPanel.removeAll();
        for (ClanListing clan : displayedClans)
        {
            resultsPanel.add(buildClanCard(clan));
            resultsPanel.add(Box.createVerticalStrut(8));
        }

        addSeeMoreButtonIfNeeded();
        statusLabel.setText("Showing " + displayedCount + " of " + totalCount + " approved clans");
        refreshResults(true);
    }

    private JPanel buildBackButtonRow()
    {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        row.setMaximumSize(new Dimension(CARD_WIDTH, 38));

        JButton button = new JButton("Back to clans", new BackArrowIcon());
        styleActionButton(button, ACTION_BACKGROUND);
        button.setFont(button.getFont().deriveFont(Font.BOLD, BACK_BUTTON_FONT_SIZE));
        button.setIconTextGap(7);
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
        String headline = normalizeDisplayText(clan.getHeadline());
        if (!headline.isEmpty())
        {
            return truncate(headline, 112);
        }

        return truncate(normalizeDisplayText(clan.getDescription()), 112);
    }

    private static String detailDescriptionText(ClanListing clan)
    {
        String description = normalizeMultilineDisplayText(clan.getDescription());
        if (!description.isEmpty())
        {
            return description;
        }

        return normalizeDisplayText(clan.getHeadline());
    }

    private static String aboutSentenceText(ClanListing clan)
    {
        String text = normalizeDisplayText(clan.getDescription());
        if (text.isEmpty())
        {
            text = normalizeDisplayText(clan.getHeadline());
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

    static String normalizeDisplayText(String value)
    {
        return normalizeMultilineDisplayText(value).replaceAll("\\s+", " ").trim();
    }

    static String normalizeMultilineDisplayText(String value)
    {
        String text = value == null ? "" : value;
        text = text
            .replace("\r\n", "\n")
            .replace('\r', '\n')
            .replaceAll("(?i)<br\\s*/?>", "\n")
            .replaceAll("(?i)</(?:p|div)>", "\n\n")
            .replaceAll("<[^>]+>", "");
        text = decodeHtmlEntities(text)
            .replace('\u00a0', ' ')
            .replaceAll("[ \\t\\x0B\\f]+", " ")
            .replaceAll(" *\\n *", "\n")
            .replaceAll("\\n{3,}", "\n\n")
            .trim();
        return text;
    }

    static Font displayTextFont(int style, float size)
    {
        return new Font(Font.DIALOG, style, Math.max(1, Math.round(size))).deriveFont(style, size);
    }

    static String emojiFontFamily()
    {
        return EMOJI_FONT_FAMILY;
    }

    private static String findEmojiFontFamily()
    {
        int[] requiredCodePoints = {0x1F389, 0x1F3B2, 0x1F4B0};
        for (String candidate : EMOJI_FONT_CANDIDATES)
        {
            Font font = new Font(candidate, Font.PLAIN, 14);
            boolean supported = true;
            for (int codePoint : requiredCodePoints)
            {
                if (!font.canDisplay(codePoint))
                {
                    supported = false;
                    break;
                }
            }

            if (supported)
            {
                return font.getFamily();
            }
        }

        return Font.DIALOG;
    }

    private static SimpleAttributeSet textAttributes(String family, int style, float size, Color color)
    {
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attributes, family == null || family.isEmpty() ? Font.DIALOG : family);
        StyleConstants.setFontSize(attributes, Math.max(1, Math.round(size)));
        StyleConstants.setBold(attributes, (style & Font.BOLD) != 0);
        StyleConstants.setItalic(attributes, (style & Font.ITALIC) != 0);
        StyleConstants.setForeground(attributes, color);
        return attributes;
    }

    private static void insertStyledText(StyledDocument document, String text, SimpleAttributeSet textAttributes, SimpleAttributeSet emojiAttributes)
    {
        for (int offset = 0; offset < text.length();)
        {
            int codePoint = text.codePointAt(offset);
            if (codePoint == 0xFE0F)
            {
                offset += Character.charCount(codePoint);
                continue;
            }

            Icon emojiIcon = emojiIcon(codePoint, StyleConstants.getFontSize(textAttributes));
            if (emojiIcon != null)
            {
                SimpleAttributeSet iconAttributes = new SimpleAttributeSet();
                StyleConstants.setIcon(iconAttributes, emojiIcon);
                try
                {
                    document.insertString(document.getLength(), " ", iconAttributes);
                }
                catch (BadLocationException ex)
                {
                    throw new IllegalStateException("Unable to render Clan Finder emoji.", ex);
                }

                offset += Character.charCount(codePoint);
                continue;
            }

            String segment = new String(Character.toChars(codePoint));
            try
            {
                document.insertString(
                    document.getLength(),
                    segment,
                    isEmojiCodePoint(codePoint) ? emojiAttributes : textAttributes
                );
            }
            catch (BadLocationException ex)
            {
                throw new IllegalStateException("Unable to render Clan Finder text.", ex);
            }

            offset += Character.charCount(codePoint);
        }

        SimpleAttributeSet paragraphAttributes = new SimpleAttributeSet();
        StyleConstants.setAlignment(paragraphAttributes, StyleConstants.ALIGN_LEFT);
        document.setParagraphAttributes(0, document.getLength(), paragraphAttributes, false);
    }

    private static boolean isEmojiCodePoint(int codePoint)
    {
        return codePoint == 0xFE0F
            || codePoint == 0x200D
            || (codePoint >= 0x2600 && codePoint <= 0x27BF)
            || (codePoint >= 0x1F000 && codePoint <= 0x1FAFF);
    }

    static boolean hasGeneratedEmojiIcon(int codePoint)
    {
        return emojiIcon(codePoint, 14) != null;
    }

    private static Icon emojiIcon(int codePoint, int fontSize)
    {
        switch (codePoint)
        {
            case 0x2620:
            case 0x2694:
            case 0x1F332:
            case 0x1F37B:
            case 0x1F389:
            case 0x1F3B2:
            case 0x1F426:
            case 0x1F43C:
            case 0x1F49D:
            case 0x1F4C5:
            case 0x1F4C6:
            case 0x1F4E3:
            case 0x1F525:
            case 0x1F4B0:
            case 0x1F4B5:
            case 0x1F5E3:
            case 0x1F916:
            case 0x1F917:
            case 0x1F91D:
            case 0x1F4CA:
                return cachedEmojiIcon(codePoint, fontSize);
            default:
                if (isEmojiCodePoint(codePoint) && codePoint != 0x200D && codePoint != 0xFE0F)
                {
                    return cachedEmojiIcon(codePoint, fontSize);
                }

                return null;
        }
    }

    private static Icon cachedEmojiIcon(int codePoint, int fontSize)
    {
        int size = Math.max(15, Math.round(fontSize * 1.25f));
        String cacheKey = codePoint + ":" + size;
        synchronized (EMOJI_ICON_CACHE)
        {
            Icon cached = EMOJI_ICON_CACHE.get(cacheKey);
            if (cached != null)
            {
                return cached;
            }

            Icon icon = new ImageIcon(drawEmojiIcon(codePoint, size));
            EMOJI_ICON_CACHE.put(cacheKey, icon);
            return icon;
        }
    }

    private static BufferedImage drawEmojiIcon(int codePoint, int size)
    {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try
        {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            switch (codePoint)
            {
                case 0x2620:
                    drawSkullEmoji(graphics, size);
                    break;
                case 0x2694:
                    drawSwordEmoji(graphics, size);
                    break;
                case 0x1F332:
                    drawTreeEmoji(graphics, size);
                    break;
                case 0x1F37B:
                    drawBeerEmoji(graphics, size);
                    break;
                case 0x1F389:
                    drawPartyEmoji(graphics, size);
                    break;
                case 0x1F3B2:
                    drawDiceEmoji(graphics, size);
                    break;
                case 0x1F426:
                    drawBirdEmoji(graphics, size);
                    break;
                case 0x1F43C:
                    drawPandaEmoji(graphics, size);
                    break;
                case 0x1F49D:
                    drawGiftHeartEmoji(graphics, size);
                    break;
                case 0x1F4C5:
                case 0x1F4C6:
                    drawCalendarEmoji(graphics, size);
                    break;
                case 0x1F4E3:
                    drawMegaphoneEmoji(graphics, size);
                    break;
                case 0x1F525:
                    drawFireEmoji(graphics, size);
                    break;
                case 0x1F4B0:
                    drawMoneyBagEmoji(graphics, size);
                    break;
                case 0x1F4B5:
                    drawMoneyEmoji(graphics, size);
                    break;
                case 0x1F5E3:
                    drawSpeakingHeadEmoji(graphics, size);
                    break;
                case 0x1F916:
                    drawRobotEmoji(graphics, size);
                    break;
                case 0x1F917:
                    drawHugEmoji(graphics, size);
                    break;
                case 0x1F91D:
                    drawHandshakeEmoji(graphics, size);
                    break;
                case 0x1F4CA:
                    drawChartEmoji(graphics, size);
                    break;
                default:
                    drawGenericEmoji(graphics, size);
                    break;
            }
        }
        finally
        {
            graphics.dispose();
        }

        return image;
    }

    private static void drawSkullEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(232, 235, 240));
        graphics.fillOval(size / 5, size / 7, size * 3 / 5, size * 3 / 5);
        graphics.fillRoundRect(size * 3 / 10, size * 3 / 5, size * 2 / 5, size / 5, size / 10, size / 10);
        graphics.setColor(new Color(48, 52, 60));
        graphics.fillOval(size / 3 - size / 12, size * 2 / 5 - size / 12, size / 6, size / 6);
        graphics.fillOval(size * 2 / 3 - size / 12, size * 2 / 5 - size / 12, size / 6, size / 6);
        graphics.fillOval(size / 2 - size / 14, size / 2, size / 7, size / 8);
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size / 4, size * 7 / 8, size * 3 / 4, size * 5 / 8);
        graphics.drawLine(size * 3 / 4, size * 7 / 8, size / 4, size * 5 / 8);
    }

    private static void drawSwordEmoji(Graphics2D graphics, int size)
    {
        graphics.setStroke(new BasicStroke(Math.max(1.2f, size / 8f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(218, 225, 234));
        graphics.drawLine(size / 4, size * 3 / 4, size * 3 / 4, size / 4);
        graphics.drawLine(size * 3 / 4, size * 3 / 4, size / 4, size / 4);
        graphics.setColor(new Color(242, 198, 71));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 10f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size / 4, size / 2, size / 2, size * 3 / 4);
        graphics.drawLine(size * 3 / 4, size / 2, size / 2, size * 3 / 4);
    }

    private static void drawTreeEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(93, 64, 45));
        graphics.fillRoundRect(size * 7 / 16, size * 3 / 5, size / 8, size / 4, size / 16, size / 16);
        graphics.setColor(new Color(42, 127, 76));
        Polygon lower = new Polygon(
            new int[] {size / 2, size / 6, size * 5 / 6},
            new int[] {size / 5, size * 7 / 10, size * 7 / 10},
            3
        );
        graphics.fillPolygon(lower);
        graphics.setColor(new Color(55, 154, 86));
        Polygon upper = new Polygon(
            new int[] {size / 2, size / 4, size * 3 / 4},
            new int[] {size / 10, size / 2, size / 2},
            3
        );
        graphics.fillPolygon(upper);
    }

    private static void drawBeerEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(245, 186, 64));
        graphics.fillRoundRect(size / 6, size / 3, size / 4, size / 2, size / 12, size / 12);
        graphics.fillRoundRect(size * 3 / 5, size / 3, size / 4, size / 2, size / 12, size / 12);
        graphics.setColor(new Color(248, 236, 188));
        graphics.fillOval(size / 8, size / 4, size / 3, size / 5);
        graphics.fillOval(size * 7 / 12, size / 4, size / 3, size / 5);
        graphics.setColor(new Color(222, 156, 43));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 13f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size * 2 / 5, size / 2, size * 3 / 5, size / 3);
        graphics.drawLine(size * 3 / 5, size / 2, size * 2 / 5, size / 3);
    }

    private static void drawPartyEmoji(Graphics2D graphics, int size)
    {
        Polygon cone = new Polygon(
            new int[] {size / 5, size * 4 / 5, size / 3},
            new int[] {size * 4 / 5, size * 2 / 5, size / 5},
            3
        );
        graphics.setColor(new Color(239, 196, 84));
        graphics.fillPolygon(cone);
        graphics.setColor(new Color(91, 151, 222));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 12f)));
        graphics.drawLine(size / 3, size * 3 / 5, size * 3 / 5, size / 2);
        graphics.setColor(new Color(220, 86, 118));
        graphics.drawLine(size / 4, size * 2 / 3, size / 2, size / 2);
        graphics.setColor(new Color(94, 196, 129));
        graphics.fillOval(size * 2 / 3, size / 8, Math.max(2, size / 6), Math.max(2, size / 6));
        graphics.setColor(new Color(226, 81, 96));
        graphics.fillOval(size * 4 / 5, size / 3, Math.max(2, size / 7), Math.max(2, size / 7));
        graphics.setColor(new Color(86, 148, 222));
        graphics.fillOval(size / 2, size / 9, Math.max(2, size / 8), Math.max(2, size / 8));
    }

    private static void drawDiceEmoji(Graphics2D graphics, int size)
    {
        int pad = Math.max(1, size / 8);
        graphics.setColor(new Color(242, 244, 248));
        graphics.fillRoundRect(pad, pad, size - pad * 2, size - pad * 2, size / 4, size / 4);
        graphics.setColor(new Color(146, 154, 166));
        graphics.drawRoundRect(pad, pad, size - pad * 2, size - pad * 2, size / 4, size / 4);
        graphics.setColor(new Color(38, 42, 49));
        int dot = Math.max(2, size / 6);
        int left = size / 3 - dot / 2;
        int mid = size / 2 - dot / 2;
        int right = size * 2 / 3 - dot / 2;
        int top = size / 3 - dot / 2;
        int bottom = size * 2 / 3 - dot / 2;
        graphics.fillOval(left, top, dot, dot);
        graphics.fillOval(right, top, dot, dot);
        graphics.fillOval(mid, mid, dot, dot);
        graphics.fillOval(left, bottom, dot, dot);
        graphics.fillOval(right, bottom, dot, dot);
    }

    private static void drawBirdEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(91, 151, 222));
        graphics.fillOval(size / 5, size / 4, size * 3 / 5, size / 2);
        graphics.setColor(new Color(128, 181, 235));
        graphics.fillOval(size / 8, size * 2 / 5, size / 3, size / 4);
        graphics.setColor(new Color(239, 182, 67));
        Polygon beak = new Polygon(
            new int[] {size * 3 / 4, size * 9 / 10, size * 3 / 4},
            new int[] {size * 2 / 5, size / 2, size * 3 / 5},
            3
        );
        graphics.fillPolygon(beak);
        graphics.setColor(new Color(34, 38, 45));
        graphics.fillOval(size * 3 / 5, size * 2 / 5, Math.max(2, size / 9), Math.max(2, size / 9));
    }

    private static void drawPandaEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(45, 49, 56));
        graphics.fillOval(size / 7, size / 8, size / 4, size / 4);
        graphics.fillOval(size * 5 / 8, size / 8, size / 4, size / 4);
        graphics.setColor(new Color(239, 241, 245));
        graphics.fillOval(size / 6, size / 5, size * 2 / 3, size * 2 / 3);
        graphics.setColor(new Color(45, 49, 56));
        graphics.fillOval(size / 3 - size / 9, size * 2 / 5 - size / 10, size / 5, size / 4);
        graphics.fillOval(size * 2 / 3 - size / 9, size * 2 / 5 - size / 10, size / 5, size / 4);
        graphics.fillOval(size / 2 - size / 14, size * 3 / 5, size / 7, size / 10);
    }

    private static void drawGiftHeartEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(221, 78, 126));
        graphics.fillOval(size / 5, size / 4, size / 3, size / 3);
        graphics.fillOval(size * 7 / 15, size / 4, size / 3, size / 3);
        Polygon point = new Polygon(
            new int[] {size / 6, size * 5 / 6, size / 2},
            new int[] {size * 2 / 5, size * 2 / 5, size * 4 / 5},
            3
        );
        graphics.fillPolygon(point);
        graphics.setColor(new Color(244, 196, 91));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size / 4, size / 2, size * 3 / 4, size / 2);
        graphics.drawLine(size / 2, size / 4, size / 2, size * 4 / 5);
    }

    private static void drawMoneyBagEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(116, 176, 89));
        graphics.fillOval(size / 5, size / 3, size * 3 / 5, size / 2);
        graphics.setColor(new Color(94, 145, 72));
        graphics.fillRoundRect(size * 2 / 5, size / 5, size / 5, size / 4, size / 8, size / 8);
        graphics.setColor(new Color(238, 212, 106));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size * 2 / 5, size / 2, size * 3 / 5, size / 2);
        graphics.drawLine(size / 2, size / 2, size / 2, size * 3 / 4);
        graphics.drawLine(size * 2 / 5, size * 3 / 4, size * 3 / 5, size * 3 / 4);
    }

    private static void drawCalendarEmoji(Graphics2D graphics, int size)
    {
        int pad = Math.max(1, size / 8);
        graphics.setColor(new Color(238, 241, 246));
        graphics.fillRoundRect(pad, pad + size / 10, size - pad * 2, size - pad * 2, size / 6, size / 6);
        graphics.setColor(new Color(211, 75, 85));
        graphics.fillRoundRect(pad, pad + size / 10, size - pad * 2, size / 4, size / 6, size / 6);
        graphics.setColor(new Color(84, 94, 110));
        int dot = Math.max(1, size / 10);
        graphics.fillOval(size / 3 - dot / 2, size / 2 - dot / 2, dot, dot);
        graphics.fillOval(size / 2 - dot / 2, size / 2 - dot / 2, dot, dot);
        graphics.fillOval(size * 2 / 3 - dot / 2, size / 2 - dot / 2, dot, dot);
        graphics.fillOval(size / 3 - dot / 2, size * 2 / 3 - dot / 2, dot, dot);
        graphics.fillOval(size / 2 - dot / 2, size * 2 / 3 - dot / 2, dot, dot);
        graphics.fillOval(size * 2 / 3 - dot / 2, size * 2 / 3 - dot / 2, dot, dot);
    }

    private static void drawMegaphoneEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(239, 241, 245));
        Polygon horn = new Polygon(
            new int[] {size / 5, size * 3 / 4, size * 3 / 4, size / 5},
            new int[] {size * 2 / 5, size / 5, size * 4 / 5, size * 3 / 5},
            4
        );
        graphics.fillPolygon(horn);
        graphics.setColor(new Color(220, 86, 118));
        graphics.fillRoundRect(size / 8, size * 2 / 5, size / 5, size / 5, size / 12, size / 12);
        graphics.setColor(new Color(91, 151, 222));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size * 4 / 5, size / 3, size * 9 / 10, size / 4);
        graphics.drawLine(size * 4 / 5, size / 2, size * 9 / 10, size / 2);
        graphics.drawLine(size * 4 / 5, size * 2 / 3, size * 9 / 10, size * 3 / 4);
    }

    private static void drawFireEmoji(Graphics2D graphics, int size)
    {
        Polygon flame = new Polygon(
            new int[] {size / 2, size * 3 / 4, size * 2 / 3, size / 2, size / 3, size / 4},
            new int[] {size / 8, size / 2, size * 5 / 6, size * 9 / 10, size * 5 / 6, size / 2},
            6
        );
        graphics.setColor(new Color(226, 81, 96));
        graphics.fillPolygon(flame);
        Polygon inner = new Polygon(
            new int[] {size / 2, size * 3 / 5, size / 2, size * 2 / 5},
            new int[] {size * 2 / 5, size * 2 / 3, size * 5 / 6, size * 2 / 3},
            4
        );
        graphics.setColor(new Color(248, 190, 73));
        graphics.fillPolygon(inner);
    }

    private static void drawMoneyEmoji(Graphics2D graphics, int size)
    {
        int pad = Math.max(1, size / 7);
        graphics.setColor(new Color(95, 169, 93));
        graphics.fillRoundRect(pad, size / 4, size - pad * 2, size / 2, size / 7, size / 7);
        graphics.setColor(new Color(185, 222, 154));
        graphics.fillOval(size / 2 - size / 6, size / 2 - size / 6, size / 3, size / 3);
        graphics.setColor(new Color(54, 118, 64));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 12f)));
        graphics.drawLine(size / 3, size / 2, size * 2 / 3, size / 2);
        graphics.drawRoundRect(pad, size / 4, size - pad * 2, size / 2, size / 7, size / 7);
    }

    private static void drawSpeakingHeadEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(236, 190, 132));
        graphics.fillOval(size / 6, size / 5, size / 2, size / 2);
        graphics.fillRoundRect(size / 3, size * 3 / 5, size / 5, size / 4, size / 12, size / 12);
        graphics.setColor(new Color(68, 76, 90));
        graphics.fillArc(size / 8, size / 8, size * 3 / 5, size / 2, 20, 200);
        graphics.setColor(new Color(52, 58, 68));
        graphics.fillRect(size / 3, size * 3 / 4, size / 3, size / 9);
        graphics.setColor(new Color(91, 151, 222));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 13f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size * 2 / 3, size * 2 / 5, size * 9 / 10, size / 3);
        graphics.drawLine(size * 2 / 3, size / 2, size * 9 / 10, size / 2);
        graphics.drawLine(size * 2 / 3, size * 3 / 5, size * 9 / 10, size * 2 / 3);
    }

    private static void drawRobotEmoji(Graphics2D graphics, int size)
    {
        int pad = Math.max(2, size / 7);
        graphics.setColor(new Color(190, 198, 210));
        graphics.fillRoundRect(pad, size / 4, size - pad * 2, size * 3 / 5, size / 8, size / 8);
        graphics.setColor(new Color(91, 151, 222));
        graphics.fillOval(size / 3 - size / 12, size / 2 - size / 12, size / 6, size / 6);
        graphics.fillOval(size * 2 / 3 - size / 12, size / 2 - size / 12, size / 6, size / 6);
        graphics.setColor(new Color(84, 94, 110));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size / 2, size / 4, size / 2, size / 10);
        graphics.drawLine(size / 3, size * 2 / 3, size * 2 / 3, size * 2 / 3);
    }

    private static void drawHugEmoji(Graphics2D graphics, int size)
    {
        graphics.setColor(new Color(245, 196, 85));
        graphics.fillOval(size / 5, size / 7, size * 3 / 5, size * 3 / 5);
        graphics.setColor(new Color(82, 68, 49));
        graphics.fillOval(size / 3, size * 2 / 5, Math.max(2, size / 10), Math.max(2, size / 10));
        graphics.fillOval(size * 3 / 5, size * 2 / 5, Math.max(2, size / 10), Math.max(2, size / 10));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 14f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawArc(size / 3, size / 2, size / 3, size / 5, 200, 140);
        graphics.setColor(new Color(245, 196, 85));
        graphics.setStroke(new BasicStroke(Math.max(2f, size / 7f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size / 6, size * 2 / 3, size / 3, size * 4 / 5);
        graphics.drawLine(size * 5 / 6, size * 2 / 3, size * 2 / 3, size * 4 / 5);
    }

    private static void drawHandshakeEmoji(Graphics2D graphics, int size)
    {
        graphics.setStroke(new BasicStroke(Math.max(2f, size / 7f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(new Color(221, 156, 89));
        graphics.drawLine(size / 5, size / 2, size / 2, size * 2 / 3);
        graphics.setColor(new Color(238, 183, 104));
        graphics.drawLine(size * 4 / 5, size / 2, size / 2, size * 2 / 3);
        graphics.setColor(new Color(103, 145, 211));
        graphics.setStroke(new BasicStroke(Math.max(2f, size / 6f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawLine(size / 8, size * 2 / 5, size / 4, size / 2);
        graphics.setColor(new Color(91, 178, 133));
        graphics.drawLine(size * 7 / 8, size * 2 / 5, size * 3 / 4, size / 2);
    }

    private static void drawChartEmoji(Graphics2D graphics, int size)
    {
        int base = size * 4 / 5;
        int barWidth = Math.max(2, size / 5);
        graphics.setColor(new Color(91, 151, 222));
        graphics.fillRoundRect(size / 6, size / 2, barWidth, base - size / 2, size / 12, size / 12);
        graphics.setColor(new Color(94, 196, 129));
        graphics.fillRoundRect(size * 2 / 5, size / 3, barWidth, base - size / 3, size / 12, size / 12);
        graphics.setColor(new Color(220, 86, 118));
        graphics.fillRoundRect(size * 2 / 3, size / 5, barWidth, base - size / 5, size / 12, size / 12);
        graphics.setColor(new Color(188, 195, 206));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 14f)));
        graphics.drawLine(size / 8, base, size * 7 / 8, base);
    }

    private static void drawGenericEmoji(Graphics2D graphics, int size)
    {
        int pad = Math.max(1, size / 8);
        graphics.setColor(new Color(70, 94, 122));
        graphics.fillRoundRect(pad, pad, size - pad * 2, size - pad * 2, size / 4, size / 4);
        graphics.setColor(new Color(236, 236, 236));
        graphics.setStroke(new BasicStroke(Math.max(1f, size / 12f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.drawArc(size / 3, size / 3, size / 3, size / 3, 200, 140);
        graphics.fillOval(size / 3, size * 2 / 5, Math.max(1, size / 10), Math.max(1, size / 10));
        graphics.fillOval(size * 3 / 5, size * 2 / 5, Math.max(1, size / 10), Math.max(1, size / 10));
    }

    private static String decodeHtmlEntities(String value)
    {
        String decoded = value
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'");

        decoded = decodeNumericEntities(decoded, HEX_HTML_ENTITY_PATTERN, 16);
        return decodeNumericEntities(decoded, DECIMAL_HTML_ENTITY_PATTERN, 10);
    }

    private static String decodeNumericEntities(String value, Pattern pattern, int radix)
    {
        Matcher matcher = pattern.matcher(value);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find())
        {
            String replacement = matcher.group(0);
            try
            {
                int codePoint = Integer.parseInt(matcher.group(1), radix);
                if (Character.isValidCodePoint(codePoint))
                {
                    replacement = new String(Character.toChars(codePoint));
                }
            }
            catch (NumberFormatException ignored)
            {
                replacement = matcher.group(0);
            }

            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(buffer);
        return buffer.toString();
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
            || requirements.isMicrophoneRequired()
            || !requirements.getApplicationInstructions().isEmpty()
            || !requirements.getNotes().isEmpty();
    }

    private static List<String> summaryRequirementLines(ClanListing clan)
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

    private static List<String> detailRequirementLines(ClanListing clan)
    {
        List<String> lines = summaryRequirementLines(clan);
        ClanRequirements requirements = clan.getRequirements();

        if (!requirements.getApplicationInstructions().isEmpty())
        {
            lines.add("Application: " + requirements.getApplicationInstructions());
        }

        if (!requirements.getNotes().isEmpty())
        {
            lines.add(requirements.getNotes());
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

    private static void styleToggleButton(JButton button)
    {
        button.setFocusPainted(false);
        button.setBackground(ACTION_BACKGROUND);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(new EmptyBorder(5, 7, 5, 7));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(28, 26));
        button.setMinimumSize(new Dimension(28, 26));
        button.setMaximumSize(new Dimension(28, 26));
        button.setToolTipText("Hide search controls");
    }

    private static String truncate(String value, int maxLength)
    {
        if (value.length() <= maxLength)
        {
            return value;
        }

        return value.substring(0, Math.max(0, maxLength - 3)).trim() + "...";
    }

    private void refreshResults(boolean resetScroll)
    {
        if (controlsWrapper != null)
        {
            controlsWrapper.revalidate();
            controlsWrapper.repaint();
        }
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

        if (resetScroll)
        {
            resetResultsScroll();
        }
    }

    private void resetResultsScroll()
    {
        resultsScrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
        resultsScrollPane.getVerticalScrollBar().setValue(0);
        SwingUtilities.invokeLater(() ->
        {
            resultsScrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
            resultsScrollPane.getVerticalScrollBar().setValue(0);
        });
    }

    private void setControlsVisible(boolean visible)
    {
        if (controlsWrapper != null && controlsWrapper.isVisible() != visible)
        {
            controlsWrapper.setVisible(visible);
            revalidate();
            repaint();
        }
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

    private static final class WrappingTextPane extends JTextPane
    {
        private final int width;

        private WrappingTextPane(int width)
        {
            this.width = width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth()
        {
            return true;
        }

        @Override
        public Dimension getPreferredSize()
        {
            Dimension preferred = super.getPreferredSize();
            return new Dimension(width, preferred.height);
        }

        @Override
        public void scrollRectToVisible(java.awt.Rectangle rect)
        {
            // Read-only text blocks are embedded inside the result list. The text
            // caret must never drive the parent JScrollPane position during layout.
        }
    }

    private static final class WrappingStyledEditorKit extends StyledEditorKit
    {
        private final ViewFactory viewFactory = new WrappingViewFactory();

        @Override
        public ViewFactory getViewFactory()
        {
            return viewFactory;
        }
    }

    private static final class WrappingViewFactory implements ViewFactory
    {
        @Override
        public View create(Element element)
        {
            String name = element.getName();
            if (AbstractDocument.ContentElementName.equals(name))
            {
                return new WrappingLabelView(element);
            }

            if (AbstractDocument.ParagraphElementName.equals(name))
            {
                return new ParagraphView(element);
            }

            if (AbstractDocument.SectionElementName.equals(name))
            {
                return new BoxView(element, View.Y_AXIS);
            }

            if (StyleConstants.ComponentElementName.equals(name))
            {
                return new ComponentView(element);
            }

            if (StyleConstants.IconElementName.equals(name))
            {
                return new IconView(element);
            }

            return new LabelView(element);
        }
    }

    private static final class WrappingLabelView extends LabelView
    {
        private WrappingLabelView(Element element)
        {
            super(element);
        }

        @Override
        public float getMinimumSpan(int axis)
        {
            if (axis == View.X_AXIS)
            {
                return 0;
            }

            return super.getMinimumSpan(axis);
        }
    }

    interface ClanFinderPanelListener
    {
        void search(ClanSearchQuery query);

        void copyClanChat(String clanChatName);

        void openClan(String slug);

        void viewClan(ClanListing clan);

        void openClanRegistration();

        void openSupportDiscord();

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

    private static final class PlusIcon implements Icon
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
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(TEXT_PRIMARY);
                g.drawLine(SIZE / 2, 3, SIZE / 2, SIZE - 3);
                g.drawLine(3, SIZE / 2, SIZE - 3, SIZE / 2);
            }
            finally
            {
                g.dispose();
            }
        }
    }

    private static final class MinusIcon implements Icon
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
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(TEXT_PRIMARY);
                g.drawLine(3, SIZE / 2, SIZE - 3, SIZE / 2);
            }
            finally
            {
                g.dispose();
            }
        }
    }

    private static final class BackArrowIcon implements Icon
    {
        private static final int WIDTH = 14;
        private static final int HEIGHT = 14;

        @Override
        public int getIconWidth()
        {
            return WIDTH;
        }

        @Override
        public int getIconHeight()
        {
            return HEIGHT;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y)
        {
            Graphics2D g = (Graphics2D) graphics.create();
            try
            {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.translate(x, y);
                g.setStroke(new BasicStroke(1.9f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(TEXT_PRIMARY);
                g.drawLine(9, 3, 5, HEIGHT / 2);
                g.drawLine(5, HEIGHT / 2, 9, HEIGHT - 3);
                g.drawLine(5, HEIGHT / 2, WIDTH - 2, HEIGHT / 2);
            }
            finally
            {
                g.dispose();
            }
        }
    }

    private static final class RequirementDotIcon implements Icon
    {
        private static final int SIZE = 6;

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
                g.setColor(TEXT_MUTED);
                g.fillOval(x, y, SIZE, SIZE);
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
