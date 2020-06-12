package at.connyduck.pixelcat.components.about.licenses

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import at.connyduck.pixelcat.R
import at.connyduck.pixelcat.components.util.extension.hide
import at.connyduck.pixelcat.databinding.CardLicenseBinding
import com.google.android.material.card.MaterialCardView

class LicenseCard
@JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {

    private val binding =
        CardLicenseBinding.inflate(LayoutInflater.from(context), this)

    init {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.LicenseCard, 0, 0)

        val name: String? = a.getString(R.styleable.LicenseCard_name)
        val license: String? = a.getString(R.styleable.LicenseCard_license)
        val link: String? = a.getString(R.styleable.LicenseCard_link)
        a.recycle()

        radius = resources.getDimension(R.dimen.license_card_radius)

        binding.licenseCardName.text = name
        binding.licenseCardLicense.text = license
        if(link.isNullOrBlank()) {
            binding.licenseCardLink.hide()
        } else {
            binding.licenseCardLink.text = link
           // setOnClickListener { LinkHelper.openLink(link, context) }
        }

    }

}

