package specificstep.com.interactors;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import specificstep.com.Models.AutoOtpModel;
import specificstep.com.Models.CashSummaryModel;
import specificstep.com.Models.ChildUserModel;
import specificstep.com.Models.Color;
import specificstep.com.Models.Company;
import specificstep.com.Models.ParentUser;
import specificstep.com.Models.ParentUserModel;
import specificstep.com.Models.Product;
import specificstep.com.Models.State;
import specificstep.com.Models.UserList;
import specificstep.com.data.entity.AutoOtpEntity;
import specificstep.com.data.entity.CashSummaryEntity;
import specificstep.com.data.entity.ColorEntity;
import specificstep.com.data.entity.CompanyEntity;
import specificstep.com.data.entity.ParentUserEntity;
import specificstep.com.data.entity.ParentUserResponse;
import specificstep.com.data.entity.ProductEntity;
import specificstep.com.data.entity.StateEntity;
import specificstep.com.data.entity.UserEntity;

@Singleton
public class UserDataMapper {

    @Inject
    public UserDataMapper() {
    }


    public List<Company> transform(List<CompanyEntity> companyEntities) {
        List<Company> companyList = null;
        if (companyEntities != null) {
            companyList = new ArrayList<>(companyEntities.size());
            for (CompanyEntity companyEntity : companyEntities) {
                companyList.add(transform(companyEntity));
            }
        }
        return companyList;
    }

    private Company transform(CompanyEntity companyEntity) {
        Company company = null;
        if (companyEntity != null) {
            company = new Company();
            company.setCompany_name(companyEntity.getName());
            company.setId(companyEntity.getId());
            company.setLogo(companyEntity.getLogo());
        }
        return company;
    }

    public List<Product> transformProducts(List<ProductEntity> productEntities) {
        List<Product> productList = null;
        if (productEntities != null) {
            productList = new ArrayList<>(productEntities.size());
            for (ProductEntity productEntity : productEntities) {
                productList.add(transformProduct(productEntity));
            }
        }
        return productList;
    }

    private Product transformProduct(ProductEntity productEntity) {
        Product product = null;
        if (productEntity != null) {
            product = new Product();
            product.setCompany_id(productEntity.getCompanyId());
            product.setProduct_logo(productEntity.getLogo());
            product.setProduct_name(productEntity.getName());
            product.setId(productEntity.getId());
        }
        return product;
    }

    public List<State> transformStates(List<StateEntity> stateEntities) {
        List<State> stateList = null;
        if (stateEntities != null) {
            stateList = new ArrayList<>(stateEntities.size());
            for (StateEntity stateEntity : stateEntities) {
                stateList.add(transformState(stateEntity));
            }
        }
        return stateList;
    }

    private State transformState(StateEntity stateEntity) {
        State state = null;
        if (stateEntity != null) {
            state = new State();
            state.setCircle_id(stateEntity.getCircleId());
            state.setCircle_name(stateEntity.getCircleName());
        }
        return state;
    }

    public List<Color> transformColors(List<ColorEntity> colors) {
        List<Color> colorList = null;
        if (colors != null) {
            colorList = new ArrayList<>(colors.size());
            for (ColorEntity colorEntity : colors) {
                colorList.add(transformColor(colorEntity));
            }
        }
        return colorList;
    }

    private Color transformColor(ColorEntity colorEntity) {
        Color color = null;
        if (colorEntity != null) {
            color = new Color();
            color.setColo_value(colorEntity.getValue());
            color.setColor_name(colorEntity.getName());
        }
        return color;
    }

    public List<UserList> transformUsers(List<UserEntity> userEntities) {
        List<UserList> userList = null;
        if (userEntities != null) {
            userList = new ArrayList<>(userEntities.size());
            for (UserEntity userEntity : userEntities) {
                userList.add(transformUser(userEntity));
            }
        }
        return userList;
    }

    private UserList transformUser(UserEntity userEntity) {
        UserList user = null;
        if (userEntity != null) {
            user = new UserList();
            user.setUser_id(userEntity.getId());
            user.setUser_name(userEntity.getFirmName());
            user.setPhone_no(userEntity.getPhoneNo());
            user.setEmail(userEntity.getEmail());
            user.setUsertype(userEntity.getUserType());
            user.setParentUserId(userEntity.getParentUserId());
        }
        return user;
    }

    public ChildUserModel map(UserEntity userList) {
        ChildUserModel childUserModel = null;
        if (userList != null) {
            childUserModel = new ChildUserModel();
            childUserModel.email = userList.getEmail();
            childUserModel.phoneNo = userList.getPhoneNo();
            try {
                childUserModel.balance = Float.parseFloat(userList.getBalance());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                childUserModel.balance = 0;
            }
            childUserModel.userType = userList.getUserType();
            childUserModel.firmName = userList.getFirmName();
            childUserModel.id = userList.getId();
            childUserModel.userName = userList.getUserName();
            childUserModel.ParentUserId = userList.getParentUserId();
        }
        return childUserModel;
    }

    public ChildUserModel mapUsers(List<UserEntity> userEntities) {
        if (userEntities != null && userEntities.size() > 0) {
            ChildUserModel childUserModel = new ChildUserModel();
            UserEntity userEntity = userEntities.get(0);
            childUserModel.setId(userEntity.getId());
            try {
                childUserModel.setBalance(Float.parseFloat(userEntity.getBalance()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            childUserModel.setEmail(userEntity.getEmail());
            childUserModel.setFirmName(userEntity.getFirmName());
            childUserModel.setPhoneNo(userEntity.getPhoneNo());
            childUserModel.setUserName(userEntity.getUserName());
            childUserModel.setUserType(userEntity.getUserType());
            childUserModel.setParentUserId(userEntity.getParentUserId());
            return childUserModel;
        }
        return null;
    }

    public List<AutoOtpEntity> mapAutoOtpList(List<AutoOtpEntity> autoOtpEntities) {
        List<AutoOtpEntity> modelList = null;
        if(autoOtpEntities != null) {
            modelList = new ArrayList<>(autoOtpEntities.size());
            for (AutoOtpEntity cashSummaryEntity : autoOtpEntities) {
                AutoOtpModel cashSummaryModel = transformCashSummary(cashSummaryEntity);
                /*try {
                    if(Float.parseFloat(cashSummaryModel.getCreditAmount()) > 0
                            || Float.parseFloat(cashSummaryModel.getDebitAmount()) > 0)
                        modelList.add(transformCashSummary(cashSummaryEntity));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }*/
            }
        }
        return modelList;
    }

    public List<CashSummaryModel> mapCashSummaryList(List<CashSummaryEntity> cashSummaryEntities) {
        List<CashSummaryModel> modelList = null;
        if(cashSummaryEntities != null) {
            modelList = new ArrayList<>(cashSummaryEntities.size());
            for (CashSummaryEntity cashSummaryEntity : cashSummaryEntities) {
                CashSummaryModel cashSummaryModel = transformCashSummary(cashSummaryEntity);
                try {
                    if(Float.parseFloat(cashSummaryModel.getCreditAmount()) > 0
                            || Float.parseFloat(cashSummaryModel.getDebitAmount()) > 0)
                    modelList.add(transformCashSummary(cashSummaryEntity));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return modelList;
    }

    private AutoOtpModel transformCashSummary(AutoOtpEntity cashSummaryEntity) {
        AutoOtpModel cashSummaryModel = null;
        if(cashSummaryEntity != null) {
            cashSummaryModel = new AutoOtpModel();
            cashSummaryModel.setSkip_otp(cashSummaryEntity.getSkip_otp());
            cashSummaryModel.setDefault_otp(cashSummaryEntity.getDefault_otp());
        }
        return cashSummaryModel;
    }

    private CashSummaryModel transformCashSummary(CashSummaryEntity cashSummaryEntity) {
        CashSummaryModel cashSummaryModel = null;
        if(cashSummaryEntity != null) {
            cashSummaryModel = new CashSummaryModel();
            cashSummaryModel.setUsername(cashSummaryEntity.getUsername());
            cashSummaryModel.setAmount(cashSummaryEntity.getAmount());
            cashSummaryModel.setCreditAmount(cashSummaryEntity.getCreditAmount());
            cashSummaryModel.setCreditDateTime(cashSummaryEntity.getCreditDateTime());
            cashSummaryModel.setDate_time(cashSummaryEntity.getDateTime());
            cashSummaryModel.setDebitAmount(cashSummaryEntity.getDebitAmount());
            cashSummaryModel.setEmail(cashSummaryEntity.getEmail());
            cashSummaryModel.setMobile(cashSummaryEntity.getMobile());
            cashSummaryModel.setPayment_id(cashSummaryEntity.getPaymentId());
            cashSummaryModel.setPaymentTo(cashSummaryEntity.getPaymentTo());
            cashSummaryModel.setpServiceId(cashSummaryEntity.getpServiceId());
            cashSummaryModel.setRemarks(cashSummaryEntity.getRemarks());
            cashSummaryModel.setCreditUserBalance(cashSummaryEntity.getCreditUserBalance());
            cashSummaryModel.setDebitUserBalance(cashSummaryEntity.getDebitUserBalance());
        }
        return cashSummaryModel;
    }

    public ParentUser mapParentUserEntity(ParentUserResponse parentUserResponse) {
        ParentUser parentUser = null;
        if(parentUserResponse != null) {
            parentUser = new ParentUser();
            parentUser.setFirstName(parentUserResponse.getParentUserEntity().getFirstName());
            parentUser.setLastName(parentUserResponse.getParentUserEntity().getLastName());
            parentUser.setPhoneNumber(parentUserResponse.getParentUserEntity().getPhoneNo());
            parentUser.setUserType(parentUserResponse.getParentUserEntity().getUserType());
            parentUser.setFirmName(parentUserResponse.getParentUserEntity().getFirmName());
            if(parentUserResponse.getUserEntities() != null) {
                parentUser.setParentUsers(transformParentUsers(parentUserResponse.getUserEntities()));
            }
        }
        return parentUser;
    }

    private List<ParentUserModel> transformParentUsers(List<ParentUserEntity> userEntities) {
        List<ParentUserModel> userModels = null;
        if(userEntities != null) {
            userModels = new ArrayList<>(userEntities.size());
            for (ParentUserEntity userEntity : userEntities) {
                if(userEntity != null) {
                    userModels.add(transformParentUserModel(userEntity));
                }
            }
        }
        return userModels;
    }

    private ParentUserModel transformParentUserModel(ParentUserEntity userEntity) {
        ParentUserModel parentUserModel = null;
        if(userEntity != null) {
            parentUserModel = new ParentUserModel();
            parentUserModel.setUserType(userEntity.getUserType());
            parentUserModel.setFirmName(userEntity.getFirmName());
            parentUserModel.setMobileNumber(userEntity.getPhoneNo());
            if(!TextUtils.isEmpty(userEntity.getFirstName()) && !TextUtils.isEmpty(userEntity.getLastName())) {
                parentUserModel.setName(userEntity.getFirstName() + " " + userEntity.getLastName());
            }else {
                parentUserModel.setName(" - ");
            }
        }
        return parentUserModel;
    }
}
