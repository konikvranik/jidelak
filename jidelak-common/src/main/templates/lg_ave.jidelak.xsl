<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output indent="yes" method="xml" encoding="UTF-8" />

	<xsl:template match="/">
		<jidelak>
			<xsl:apply-templates select="/jidelak/config|/html" />
		</jidelak>
	</xsl:template>

	<xsl:template match="/jidelak/config|/html">
		<config>
			<xsl:call-template name="restaurant" />
		</config>

	</xsl:template>

	<xsl:template name="restaurant">
		<restaurant version="1.2">
			<id>praha-lunch-garden-avenir</id>
			<name>Lunch Garden Avenir</name>
			<phone>+420 731 401 714</phone>
			<e-mail>pavel.pechaty.ml@cateringmelodie.cz</e-mail>
			<web>http://fgkavcihory.cateringmelodie.cz/cz/</web>
			<city>Praha 5</city>
			<country>Česká republika</country>
			<address>Radlická 714/113a</address>
			<zip>158 00</zip>

			<source time="relative" base="day" firstDayOfWeek="Po"
				timeOffset="-1" encoding="cp1250" dateFormat="d.M.y" locale="cs_CZ"
				url="http://lgavenir.cateringmelodie.cz/cz/denni-menu-tisk.php" />
			<source time="relative" base="day" firstDayOfWeek="Po"
				timeOffset="-1" encoding="cp1250" dateFormat="d.M.y" locale="cs_CZ"
				url="http://lgavenir.cateringmelodie.cz/cz/denni-menu-pristi-tyden-tisk.php" />

			<open>
				<term day-of-week="Po" from="8:00" to="17:00" />
				<term from="11:00" to="14:30" description="polední menu" />
				<term day-of-week="So" closed="true" />
				<term day-of-week="Ne" closed="true" />
			</open>

			<menu>
				<xsl:apply-templates
					select="//table[@class='tb_jidelak' and position() = 1]/tbody/tr[1]/td"
					mode="days" />
			</menu>
		</restaurant>
	</xsl:template>

	<xsl:template match="td" mode="days">
		<xsl:variable name="pos" select="position()" />
		<xsl:variable name="ref-time"
			select="substring-before(/html/body/div[@class='okno-tisk']/center/div[@class='vnitrek-tisk']/p[@class='centrovani velka']/b/text(), ' ') " />

		<xsl:apply-templates
			select="../../tr[2]/td[$pos]/p[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'1-normal'" />
			<xsl:with-param name="dish" select="'soup'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../tr[position() &gt; 2 and position() &lt; 6]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'1-normal'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../tr[6]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'2-vegetarian'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../tr[7]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'3-salad'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and ( position() = 1)]/tbody/tr[2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'4-superior'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and (position() = 2 )]/tbody/tr[2]/td[$pos = position() and normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'5-live'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>
		<xsl:apply-templates
			select="../../../following-sibling::table[@class='tb_jidelak' and position() = 3]/tbody/tr[2]/td[normalize-space(translate(.,'&#160;', ' ')) != '']">
			<xsl:with-param name="type" select="'6-pasta'" />
			<xsl:with-param name="dish" select="'dinner'" />
			<xsl:with-param name="ref-time" select="$ref-time" />
			<xsl:with-param name="time" select="$pos" />
		</xsl:apply-templates>

	</xsl:template>

	<xsl:template match="td|td/p">
		<xsl:param name="type" />
		<xsl:param name="dish" />
		<xsl:param name="time" />
		<xsl:param name="ref-time" />
		<meal order="{position()}" category="{$type}" dish="{$dish}"
			time="{$time}" ref-time="{$ref-time}">
			<title>
				<xsl:value-of select="." />
			</title>
			<description></description>
			<xsl:choose>
				<xsl:when test="$dish = 'soup'">
					<price>18 Kč</price>
				</xsl:when>
				<xsl:when test="$type = '4-superior'">
					<price>90 Kč</price>
				</xsl:when>
				<xsl:when test="$type = '5-live'">
					<price>110 Kč</price>
				</xsl:when>
				<xsl:when test="$type = '6-pasta'">
					<price>84 Kč</price>
				</xsl:when>
				<xsl:otherwise>
					<price>74 Kč</price>
				</xsl:otherwise>
			</xsl:choose>
		</meal>
	</xsl:template>

</xsl:stylesheet>